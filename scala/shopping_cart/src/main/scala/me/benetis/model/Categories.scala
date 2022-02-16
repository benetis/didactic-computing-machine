package me.benetis.model

import java.util.UUID

trait Categories[F[_]] {
    def findAll: F[List[Brand]]
    def create(category: Category): F[BrandId]
}

case class CategoryName(value: String) extends AnyVal
case class CategoryId(uuid: UUID) extends AnyVal
case class Category(uuid: CategoryId, name: CategoryName)
