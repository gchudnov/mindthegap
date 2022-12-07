package com.github.gchudnov.mtg

import scala.quoted.*

object DiagramMacro:

  inline def names[T](inline xs: T*): List[String] =
    ${namesImpl('xs)}

  private def namesImpl[T: Type](exprs: Expr[Seq[T]])(using qctx: Quotes): Expr[List[String]] =
    import qctx.reflect.*

    println(exprs.asTerm.show(using Printer.TreeStructure))

    def showExpr(e: Expr[_]): Expr[String] =
      '{${Expr(e.show)}.toString()}

    val exprNames: Seq[Expr[String]] = exprs match
      case Varargs(es) =>
        es.map { e =>
          e.asTerm match {
            case Literal(c: Constant) => Expr(c.value.toString)
            case _ => showExpr(e)
          }
        }
      case e => 
        List(Expr("other"))
        // List(showExpr(e))

    val namesExpr: Expr[List[String]] = Expr.ofList(exprNames)

    namesExpr

/*
  inline def debug(inline exprs: Any*): Unit =
    ${debugImpl('exprs)}

  private def debugImpl(exprs: Expr[Seq[Any]])(using Quotes): Expr[Unit] =
    import quotes.reflect.*

    def showWithValue(e: Expr[_]): Expr[String] =
      '{${Expr(e.show)} + " = " + $e}

    val stringExps: Seq[Expr[String]] = exprs match
      case Varargs(es) =>
        es.map { e =>
          e.asTerm match {
            case Literal(c: Constant) => Expr(c.value.toString)
            case _ => showWithValue(e)
          }
        }
      case e => List(showWithValue(e))

    val concatenatedStringsExp = stringExps
      .reduceOption((e1, e2) => '{$e1 + ", " + $e2})
      .getOrElse('{""})

    '{println($concatenatedStringsExp)}



import scala.quoted.*

object Transpiler:

  final case class MyState()

  /// TRACE

  inline def trace[T](inline x: T): Unit =
    ${traceImpl('x)}

  private def traceImpl[T: Type](expr: Expr[T])(using qctx: Quotes): Expr[Unit] =
    import qctx.reflect.*

    def collectAST(tree: Tree): MyState =
      val acc = new TreeAccumulator[MyState]:
        override def foldTree(x: MyState, tree: Tree)(owner: Symbol): MyState = tree match
          case Literal(c) =>
            c match {
              case y: BooleanConstant(value) =>
                BoolVal(value)
              case _ =>
                NothingVal()
            }
          case Block(ss, t) =>
            println("BLOCK: " + t)
            foldTrees(x, ss)(owner)
          case other =>
            println("OTHER: " + other)
            MyState()
            // foldOverTree(x, tree)(owner)
      acc.foldOverTree(MyState(), tree)(Symbol.noSymbol)

      //   def foldTree(syms: List[Symbol], tree: Tree)(owner: Symbol): List[Symbol] = tree match
      //     case ValDef(_, _, rhs) =>
      //       val newSyms = tree.symbol :: syms
      //       foldTree(newSyms, body)(tree.symbol)
      //     case other =>
      //       println(other)
      //       foldOverTree(syms, tree)(owner)
      // acc(Nil, tree)

    val tree: Tree = expr.asTerm

    // println(tree.show(using Printer.TreeStructure))

    collectAST(tree)

    '{()}

  /// DEBUG

  inline def debug(inline exprs: Any*): Unit =
    ${debugImpl('exprs)}

  private def debugImpl(exprs: Expr[Seq[Any]])(using Quotes): Expr[Unit] =
    import quotes.reflect.*

    def showWithValue(e: Expr[_]): Expr[String] =
      '{${Expr(e.show)} + " = " + $e}

    val stringExps: Seq[Expr[String]] = exprs match
      case Varargs(es) =>
        es.map { e =>
          e.asTerm match {
            case Literal(c: Constant) => Expr(c.value.toString)
            case _ => showWithValue(e)
          }
        }
      case e => List(showWithValue(e))

    val concatenatedStringsExp = stringExps
      .reduceOption((e1, e2) => '{$e1 + ", " + $e2})
      .getOrElse('{""})

    '{println($concatenatedStringsExp)}

  /// DEBUG-SINGLE

  inline def debugSingle(inline expr: Any): Unit =
    ${ debugSingleImpl('expr) }

  private def debugSingleImpl(expr: Expr[Any])(using Quotes): Expr[Unit] =
    '{ println("Value of " + ${ Expr(expr.show) } + " is " + $expr) }


// macro
object PrintTree {
  inline def printTree[T](inline x: T): Unit = ${printTreeImpl('x)}
  def printTreeImpl[T: Type](x: Expr[T])(using qctx: Quotes): Expr[Unit] =
    import qctx.reflect.*
    println(x.asTerm.show(using Printer.TreeStructure))
    '{()}
}

// usage
printTree {
  (s: String) => s.length
}

// output
Inlined(None, Nil, Block(Nil, Block(List(DefDef("$anonfun", List(TermParamClause(List(ValDef("s", TypeIdent("String"), None)))), Inferred(), Some(Block(Nil, Apply(Select(Ident("s"), "length"), Nil))))), Closure(Ident("$anonfun"), None))))

// after a while the above representation becomes semi-readable


 */
