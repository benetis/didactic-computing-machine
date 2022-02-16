package me.benetis.model

import java.util.UUID

trait Users[F[_]] {
  def find(
      username: UserName
  ): F[Option[UserWithPassword]]
  def create(
      username: UserName,
      password: EncryptedPassword
  ): F[UserId]
}

case class UserId(value: UUID)
case class UserName(value: String) extends AnyVal
case class Password(value: String) extends AnyVal
case class EncryptedPassword(value: String) extends AnyVal
case class UserWithPassword(
    id: UserId,
    name: UserName,
    password: EncryptedPassword
)


case class User(id: UserId, name: UserName)
