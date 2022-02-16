package me.benetis.model

trait ShoppingCart[F[_]] {
  def add(
    userId: UserId,
    itemId: ItemId,
    quantity: Quantity
  ): F[Unit]
  def get(userId: UserId): F[CartTotal]
  def delete(userId: UserId): F[Unit]
  def removeItem(userId: UserId, itemId: ItemId): F[Unit]
  def update(userId: UserId, cart: Cart): F[Unit]
}

case class Quantity(value: Int) extends AnyVal
case class Cart(items: Map[ItemId, Quantity]) extends AnyVal

case class CartItem(item: Item, quantity: Quantity)

/* If I was to design it, I would probably go with fn: items -> total instead */
case class CartTotal(items: List[CartItem], total: Money)
