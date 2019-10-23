
class Graphs {

  val down = "│"
  val right = "└"
  val downright = "├"
  val space = " "

  type ParsedInput = List[(String, String)]

  object ParsedInput {
    private val parse_outer_list = """(?s)\[(.*)\]""".r.unanchored
    private val parse_tuple = """\[("[^"]+") (nil|"[^"]+")\]""".r.unanchored

    private def parse_name(str: String): String = {
      if (str.head == '"' && str.last == '"') str.init.tail
      else if (str == "nil") null
      else throw new AssertionError("")
    }

    def apply(str: String): ParsedInput = str match {
      case parse_outer_list(inner_tuples) =>
        parse_tuple.findAllMatchIn(inner_tuples).map { m =>
          parse_name(m.group(1)) -> parse_name(m.group(2))
        }.toList
    }
  }

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

  def read_tree(inp: ParsedInput): Tree = {
    // We could do this without mutability, by folding with an immutable map.
    // But, this is simpler, readable, and compartmentalized.
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
        // This case means we didn't correctly add some of the children, because the input order isn't what we expected.
        assert(rem_children.keySet.isEmpty, "We expect the input to be in a particular order.")
        tree
    }
  }


  case class Tree(key: String, children: List[Tree] = Nil) {
    /**
      * @return A list of strings, representing each of the lines of the output, in order from top to bottom.
      *         Note that this list will always be non-empty.
      */
    def calcToString: List[String] = {

      children.reverse.map(_.calcToString) match {
        case Nil => List(key)

        case raw_last_lines :: rev_rest_lines =>
          val middle_lines = rev_rest_lines.reverse.flatMap {
            case first_middle :: rest_middle =>
              downright + first_middle :: rest_middle.map(down + _)
          }

          val last_lines = raw_last_lines match {
            case first_of_last :: rest_last =>
              right + first_of_last :: rest_last.map(space + _)
          }

          key :: middle_lines ++ last_lines
      }
    }

    override def toString: String = calcToString.mkString("\n")
  }

  for (ex <- examples)
    println(s"${read_tree(ex)}\n")
}

object Graphs extends Graphs with App
