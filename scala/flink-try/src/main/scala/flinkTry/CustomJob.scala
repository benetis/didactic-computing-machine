package flinkTry

import java.lang.{Long => JLong}
import java.time.Duration
import java.util.concurrent.ThreadLocalRandom

import org.apache.flink.api.common.functions.AggregateFunction
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.api.datastream.{DataStream => JDataStream}
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.util.Collector

final case class Payment(userId: String, amount: Double)
final case class Agg(count: Long, sum: Double)
final case class Alert(
    userId: String,
    windowStart: Long,
    windowEnd: Long,
    txnCount: Long,
    totalAmount: Double
)

given TypeInformation[Payment] = TypeInformation.of(classOf[Payment])
given TypeInformation[Agg] = TypeInformation.of(classOf[Agg])
given TypeInformation[Alert] = TypeInformation.of(classOf[Alert])

object CustomJob:
  def main(args: Array[String]): Unit =
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    val seq: JDataStream[JLong] = env.fromSequence(0L, JLong.MAX_VALUE)

    val payments: JDataStream[Payment] =
      seq.map { case i: JLong =>
        val userIdx = ((i % 500) + 1).toInt
        val userId = String.format("user_%04d", Integer.valueOf(userIdx))
        val amount = 1 + ThreadLocalRandom.current().nextInt(500).toDouble
        Payment(userId, amount)
      }

    val alerts =
      payments
        .keyBy((p: Payment) => p.userId)
        .window(TumblingProcessingTimeWindows.of(Duration.ofMinutes(1)))
        .aggregate(
          new AggregateFunction[Payment, Agg, Agg]:
            override def createAccumulator(): Agg = Agg(0L, 0.0)
            override def add(v: Payment, a: Agg): Agg =
              Agg(a.count + 1, a.sum + v.amount)
            override def getResult(a: Agg): Agg = a
            override def merge(a: Agg, b: Agg): Agg =
              Agg(a.count + b.count, a.sum + b.sum)
          ,
          new ProcessWindowFunction[Agg, Alert, String, TimeWindow]:
            override def process(
                key: String,
                ctx: ProcessWindowFunction[
                  Agg,
                  Alert,
                  String,
                  TimeWindow
                ]#Context,
                elements: java.lang.Iterable[Agg],
                out: Collector[Alert]
            ): Unit =
              val it = elements.iterator()
              if it.hasNext then
                val a = it.next()
                if a.count >= 5 || a.sum >= 1000.0 then
                  out.collect(
                    Alert(
                      key,
                      ctx.window.getStart,
                      ctx.window.getEnd,
                      a.count,
                      a.sum
                    )
                  )
        )

    alerts.print()
    env.execute("Fraud Alerts (Scala 3 on Flink 2)")
