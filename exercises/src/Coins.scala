import scala.collection.immutable.SortedMap

class Coins {

  def gen_coin_names: SortedMap[Int, String] = SortedMap(
    1 -> "penny",
    5 -> "nickel",
    10 -> "dime",
    25 -> "quarter",
    100 -> "loonie",
    200 -> "toonie"
  )

  /** lazy so we may override it */
  lazy val coin_names: SortedMap[Int, String] = gen_coin_names

  /**
    * Compute the minimum number and types of coins we need.
    * Essentially constant time efficiency, assuming division and remainder are constant.
    * @param amt The amount of change that the values of the coins should sum to.
    * @return A map from coin values to the number of coins needed.
    *         The dot product of the keys and the values should produce the amount.
    */
  def calc_coins(amt: Int): Map[Int, Int] = {

    /**
      * Our solution is a greedy algorithm that uses as many of the largest coin available as possible,
      * and then uses recursion on the rest of the coin denominations and the remaining amount.
      *
      * <br>
      *
      * We know that this algorithm will produce the minimum total coins because of the way that
      * most of the canadian coins denominations divide the next denomination,
      * and for the special case of the dime, we can create 30 cents by using a quarter and a nickel
      * instead of 3 dimes.
      *
      * @param rem_coins The coins that we haven't yet attempted to use.
      *                  Starts off as the entire list of coins, sorted from largest to smallest,
      *                  and we remove the largest in each recursive call.
      */
    def loop(rem_coins: List[Int], rem_amt: Int): Map[Int, Int] = rem_coins match {
      case Nil => Map()
      case value :: rest_coins =>
        val num_coins = rem_amt / value
        val new_amt = rem_amt % value
        loop(rest_coins, new_amt) + (value -> num_coins)
    }
    loop(coin_names.keys.toList.reverse, amt)
  }



  /** Prints a nice, fully worded sentence containing the answer produced by [[Coins#calc_coins]]. */
  def pretty_print_coins(amt: Int): Unit = {
    def pluralize(str: String, nat: Int): String =
      if (nat == 1) str
      else if (str.last == 'y') str.dropRight(1) + "ies"
      else str + "s"

    val ans = calc_coins(amt)

    for {
      (value, name) <- coin_names
      num_coins = ans(value) if num_coins != 0
    }
      println(num_coins + " " + pluralize(name, num_coins))
    println(ans.valuesIterator.sum + " total coins\n")
  }
}

/** The default `Coins` object. */
object Coins extends Coins

object BonusCoins extends Coins {
  override def gen_coin_names: SortedMap[Int, String] = super.gen_coin_names + (53 -> "bonus coin")

  /**
    * The general idea with this is to use the greedy algorithm for the subset of coins that we know it works for.
    * Since this is the case for canadian coins, we can extend our previous algorithm
    * by trying all possible numbers of the 53-cent coin, and then using the previous algorithm for the remaining amount,
    * and finding the best combination of them all.
    * <br>
    * The algorithm has linear efficiency w.r.t. the amount.
    */
  override def calc_coins(amt: Int): Map[Int, Int] = {

    val possible_solns =
      for (num_53 <- 0 to (amt / 53)) yield
        Coins.calc_coins(amt - num_53 * 53) + (53 -> num_53)

    possible_solns.minBy(_.values.sum)
  }
}




object TestCoins extends App {

  // Change this boolean to switch between the normal and the bonus questions.
  val CoinObj: Coins = if (false) Coins else BonusCoins

  import CoinObj.pretty_print_coins

  println("653: ")
  pretty_print_coins(653)

  println("63: ")
  pretty_print_coins(63)

  println("132: ")
  pretty_print_coins(132)
}
