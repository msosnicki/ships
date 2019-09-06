package com.ssn.ships

import cats.effect.Sync

object console {
  def readLine[F[_]](implicit S: Sync[F]): F[String] =
    S.delay(Console.in.readLine)
  def printLine[F[_]](str: String)(implicit S: Sync[F]): F[Unit] =
    S.delay(Console.out.println(str))
  def print[F[_]](str: String)(implicit S: Sync[F]): F[Unit] =
    S.delay(Console.out.print(str))
}
