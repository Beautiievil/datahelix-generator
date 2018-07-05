package com.scottlogic.deg.dto

case class NumericField(
  name:String,
  nullPrevalence:Number,
  distribution: AbstractDistribution
) extends AbstractField