import Trees.ParsedInput

import scala.io.StdIn
import scala.util.Try

object Interact extends App {
  private def choice[T](in: String,
                        choice1: Seq[Char], do1: () => T,
                        choice2: Seq[Char], do2: () => T, default: () => T) = {
    if (choice1 contains in.trim.head.toLower)
      do1()
    else if (choice2 contains in.trim.head.toLower)
      do2()
    else default()
  }

  def choose_exercise(): Unit = {
    choice(
      StdIn.readLine("Which exercise would you like to explore? Type 'coins' or 'trees'. "),
      "1c", choose_coins _,
      "2t", trees _,
      choose_exercise _
    )
  }

  def choose_coins(): Unit = {
    choice(
      StdIn.readLine("Which coins exercise would you like to explore? Type 'regular' or 'bonus'. "),
      "1r", () => coins(Coins),
      "2b", () => coins(BonusCoins),
      choose_exercise _
    )
  }

  def coins(CoinsObj: Coins): Unit = {
    object TestCoins extends TestCoins(CoinsObj)

    // Initialize TestCoins
    val _ = TestCoins.examples

    while (true) {
      val in = StdIn.readLine("Type in a total amount, or 'randomize' to do a value from 1 to 9999. ")
      if (Try(in.toInt).isSuccess)
        CoinsObj.pretty_print_coins(in.toInt)
      else if (in.trim.head.toLower == 'r')
        TestCoins.generate()
      else
        choose_exercise()
    }
  }

  def trees(): Unit = {

    // Initialize TestTrees
    val _ = TestTrees.examples

    while (true) {
      val in = StdIn.readLine("Enter a list of tuples, or 'randomize' for a random tree. ")

      if (in.trim.head.toLower == 'r')
        TestTrees.generate()

      else if (in.trim.head.toLower == '[') {

        def count_brackets(ln: String): Int = ln.toSeq.map {
          case '[' => 1
          case ']' => -1
          case  _  => 0
        }.sum

        // keep reading until we balance the `[` and `]` brackets

        val all_lines = collection.mutable.ArrayBuffer[String](in)
        var bracket_count = count_brackets(all_lines.last)

        while (bracket_count != 0) {
          all_lines += StdIn.readLine()
          bracket_count += count_brackets(all_lines.last)
        }

        println(Trees.read_tree(ParsedInput(all_lines.mkString("\n"))))
      }

      else choose_exercise()
    }
  }

  choose_exercise()
}
