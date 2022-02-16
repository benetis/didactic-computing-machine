package me.benetis.model.third_party

import me.benetis.model._


trait PaymentClient[F[_]] {
  def process(payment: Payment): F[PaymentId]
}

case class Payment(
    id: UserId,
    total: Money,
    card: Card
)

case class Card()
