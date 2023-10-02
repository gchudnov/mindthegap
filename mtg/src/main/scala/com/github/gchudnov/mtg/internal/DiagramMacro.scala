package com.github.gchudnov.mtg.internal

import scala.quoted.*

/**
 * Diagram Macro to deduce variable names in a list;
 *
 * Used to name intervals on a diagram without passing an explicit list f names.
 *
 * To debug, use:
 * {{{
 *   println(other.show(using Printer.TreeStructure))
 * }}}
 */
object DiagramMacro:

  transparent inline def varNames(inline expr: Iterable[Any]): List[String] =
    ${ varNamesImpl('expr) }

  private def varNamesImpl(expr: Expr[Any])(using Quotes): Expr[List[String]] =
    import quotes.reflect.*

    def extract(tree: Tree): List[String] = tree match
      case Inlined(_, _, expr) => extract(expr)
      case Apply(_, List(Typed(Repeated(args, _), _))) =>
        args.flatMap((a) =>
          a match
            case Ident(name) =>
              List(name)
            case other =>
              List.empty[String] // unsupported expression, ignore
        )
      case other =>
        List.empty[String] // unsupported expression, ignore

    val names = extract(expr.asTerm)
    Expr(names)
