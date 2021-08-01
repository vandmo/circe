package io.circe.derivation

import scala.deriving.Mirror
import scala.compiletime.constValue
import Predef.genericArrayOps
import io.circe.{Encoder, Json}
import io.circe.syntax._

trait ConfiguredEnumEncoder[A] extends Encoder[A]
object ConfiguredEnumEncoder:
  inline final def derived[A](using conf: EnumConfiguration = EnumConfiguration.default)(using mirror: Mirror.SumOf[A]): ConfiguredEnumEncoder[A] =
    // Only used to validate if all cases are singletons
    summonSingletonCases[mirror.MirroredElemTypes, A](constValue[mirror.MirroredLabel])
    val labels = summonLabels[mirror.MirroredElemLabels].toArray.map(conf.encodeTransformNames)
    new ConfiguredEnumEncoder[A]:
      def apply(a: A): Json = labels(mirror.ordinal(a)).asJson
  
  inline final def derive[A: Mirror.SumOf](encodeTransformNames: String => String = EnumConfiguration.default.encodeTransformNames): Encoder[A] =
    given EnumConfiguration = EnumConfiguration.default.withDecodeTransformNames(encodeTransformNames)
    derived[A]
