package me.benetis.model

import java.util.UUID

trait Items[F[_]] {
  def findAll: F[List[Item]]
  def findBy(brand: BrandName): F[List[Item]]
  def findById(itemId: ItemId): F[Option[Item]]
  def create(item: CreateItem): F[ItemId]
  def update(item: UpdateItem): F[Unit]
}

case class ItemId(value: UUID) extends AnyVal
case class ItemName(value: String) extends AnyVal
case class ItemDescription(value: String) extends AnyVal
case class Money(value: BigDecimal) extends AnyVal

case class Item(
    uuid: ItemId,
    name: ItemName,
    description: ItemDescription,
    price: Money,
    brand: Brand,
    category: Category
)

case class CreateItem(
    name: ItemName,
    description: ItemDescription,
    price: Money,
    brandId: BrandId,
    categoryId: CategoryId
)

case class UpdateItem(
    id: ItemId,
    price: Money
)
