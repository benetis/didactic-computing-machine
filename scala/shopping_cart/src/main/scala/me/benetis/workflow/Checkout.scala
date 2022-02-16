package me.benetis.workflow

import cats.Monad
import me.benetis.model.third_party._
import me.benetis.model._
import cats.MonadThrow
import cats.data._
import cats.implicits._
import cats.effect._
import cats.effect.implicits._
import scala.concurrent.duration._

case object EmptyCartError

final case class Checkout[F[_]: Monad](
    payments: PaymentClient[F],
    cart: ShoppingCart[F],
    orders: Orders[F]
) {
  private def ensureNonEmpty[A](xs: List[A]): F[NonEmptyList[A]] =
    MonadThrow[F].fromOption(
      NonEmptyList.fromList(xs),
      EmptyCartError
    )

  def process(userId: UserId, card: Card): F[OrderId] =
    for {
      c <- cart.get(userId)
      its <- ensureNonEmpty(c.items)
      pid <- payments.process(Payment(userId, c.total, card))
      oid <- orders.create(userId, pid, its, c.total)
      _ <- cart.delete(userId).attempt.void
    } yield oid

  def retry[A](fa: F[A]): F[A] =
    Temporal[F].sleep(50.milliseconds) >> retry (fa)
}
