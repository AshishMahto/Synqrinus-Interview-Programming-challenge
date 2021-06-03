import scala.util.Random

class Trees {

  // unicode characters, saved for ease of use.
  val down = "│"
  val right = "└"
  val downright = "├"
  val space = " "

  type ParsedInput = List[(String, String)]

  object ParsedInput {
    private val parse_outer_list = """(?s)\[(.*)]""".r.unanchored
    private val parse_tuple = """\[("[^"]+") (nil|null|"[^"]+")]""".r.unanchored

    private def parse_name(str: String): String = {
      if (str.head == '"' && str.last == '"') str.init.tail
      else if (str == "nil") null
      else throw new AssertionError("")
    }

    def apply(raw_inp: String): ParsedInput = raw_inp match {
      case parse_outer_list(inner_tuples) =>
        parse_tuple.findAllMatchIn(inner_tuples).map { m =>
          parse_name(m.group(1)) -> parse_name(m.group(2))
        }.toList
    }
  }

  /**
    * Given some parsed input (a list of pairs of strings),
    * generate the corresponding [[Trees.Tree Tree]] data structure.
    * @param inp In the exercise instructions, we were told that "The order will not be randomized." <br>
    *   I have taken this assumption to mean that the tuples will be given to us in a very particular order:
    *   it must be the case that any person (or `key` of our tree) must appear on the left side of a pair in
    *   the list before it is used on the right side of a pair. <br>
    *   More formally,
    * {{{for (i <- 1 until inp.length) yield {
    *    val (_, right) = inp(i)
    *    val validate   = inp.take(i).exists { case (left, _) => left == right }
    *    if (! validate) throw new AssertionError()
    *  } }}} should not throw an assertion error.
    */
  def read_tree(inp: ParsedInput): Tree = {
    // We could do this without mutability, by folding with an immutable map.
    // But, using mutability here is simpler, readable, and compartmentalized.
    import scala.collection.mutable

    val rem_children = mutable.Map.empty[String, List[Tree]].withDefaultValue(Nil)

    for ((key, parent) <- inp.reverse) {
      val added_tree = Tree(key, rem_children.remove(key).toList.flatten)
      rem_children(parent) = added_tree :: rem_children(parent)
    }

    // This is basically an error-checking version of `rem_children(null).head`
    rem_children.remove(null).toList.flatten match {
      case Nil => throw new AssertionError("We expect the input to contain the root.")
      case _ :: _ :: _ => throw new AssertionError("We expect the input to contain exactly 1 root.")
      case List(tree) =>
        // This case means we didn't correctly add some of the children,
        // because the input order isn't what we expected.
        assert(rem_children.keySet.isEmpty, "We expect the input to be in a particular order.")
        tree
    }
  }


  /** A natural definition for a tree data structure.
    * @param key The string that is held in each node of the tree.
    * @param children The roots of the subtrees of this tree. `Nil` if `this` is a leaf node.
    */
  case class Tree(key: String, children: List[Tree] = Nil) {

    /** Compute the string representation of this tree. The main function in solving this exercise.
      * @return A list of strings, representing each of the lines of the output, in order from top to bottom.
      *         Note that this list will always be non-empty.
      */
    def calcToString: ::[String] = {

      children.reverse.map(_.calcToString) match {
        case Nil => ::(key, Nil)

        case raw_last_lines :: rev_rest_lines =>
          val middle_lines = rev_rest_lines.reverse.flatMap {
            case first_middle :: rest_middle =>
              downright + first_middle :: rest_middle.map(down + _)
          }

          val last_lines = raw_last_lines match {
            case first_of_last :: rest_last =>
              right + first_of_last :: rest_last.map(space + _)
          }

          ::(key, middle_lines ++ last_lines)
      }
    }

    override def toString: String = calcToString.mkString("\n") + "\n"
  }

  def pretty_print_tree(raw_inp: String): Unit =
    println(read_tree(ParsedInput(raw_inp)))
}

/** Default `Trees` object */
object Trees extends Trees with App

object TestTrees extends App {
  import Trees.ParsedInput

  val examples: List[ParsedInput] = List(
    List(
      "A" -> null,
      "B" -> "A",
      "C" -> "B",
      "D" -> "C"
    ),
    List(
      "A" -> null,
      "B" -> "A",
      "C" -> "B",
      "D" -> "B",
      "E" -> "A"
    ),
    List(
      "A" -> null,
      "B" -> "A",
      "C" -> "B",
      "D" -> "C",
      "E" -> "A"
    )
  )

  for (ex <- examples) println(Trees.read_tree(ex))

  /**
    * Generates a random tree with at most 26 nodes.
    * @param probability A percentage chance that we add another node.
    *                    For example, if this is 95, the expected value for the size of the tree
    *                    is 20 nodes.
    */
  def generate(probability: Int = 95): Unit = {
    import scala.collection.mutable

    val letters = ('B' to 'Z').iterator

    val parsedInput = mutable.SortedMap[String, String]("A" -> null)

    while (letters.hasNext && Random.nextInt(100) < probability) {
      val next_char = letters.next()
      val parent = Random.between('A'.toInt, next_char.toInt).toChar
      parsedInput(next_char.toString) = parent.toString
    }

    println("\nInput:")
    println(
      parsedInput.toSeq.map {
        case (l, null) => s"""["$l" nil]"""
        case (l, r)    => s"""["$l" "$r"]"""
      }.mkString("[", "\n ", "]")
    )
    println("\nOutput:")
    println(Trees.read_tree(parsedInput.toList))
  }

  generate()
}