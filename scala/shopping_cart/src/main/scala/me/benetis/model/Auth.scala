package me.benetis.model

import derevo.derive
import derevo.circe.encoder
import io.circe._
import cats._
import cats.implicits._

trait Auth[F[_]] {
  def findUser(token: JwtToken): F[Option[User]]
  def newUser(username: UserName, password: Password): F[JwtToken]
  def login(username: UserName, password: Password): F[JwtToken]
  def logout(token: JwtToken, username: UserName): F[Unit]
}

case class JwtToken(value: String) extends AnyVal
