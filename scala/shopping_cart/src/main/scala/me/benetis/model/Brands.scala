package me.benetis.model

import java.util.UUID

trait Brands[F[_]] {
    def findAll: F[List[Brand]]
    def create(brandName: BrandName): F[BrandId]
}

case class BrandName(value: String) extends AnyVal
case class BrandId(uuid: UUID) extends AnyVal
case class Brand(uuid: BrandId, name: BrandName)
