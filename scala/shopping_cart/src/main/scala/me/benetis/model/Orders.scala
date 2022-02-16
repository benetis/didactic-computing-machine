package me.benetis.model

import cats.data.NonEmptyList
import java.util.UUID

trait Orders[F[_]] {
  def get(
      userId: UserId,
      orderId: OrderId
  ): F[Option[Order]]
  def findBy(userId: UserId): F[List[Order]]
  def create(
      userId: UserId,
      paymentId: PaymentId,
      items: NonEmptyList[CartItem],
      total: Money
  ): F[OrderId]
}

case class OrderId(uuid: UUID) extends AnyVal
case class PaymentId(uuid: UUID) extends AnyVal
case class Order(
    id: OrderId,
    pid: PaymentId,
    items: Map[ItemId, Quantity],
    total: Money
)
