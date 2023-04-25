# TODO

- add split

- when the screen is small, the should be a hamburger menu on the left -- see react toolkit

- links in text to the outer sites should have an icon with an arrow.

- add tests for negative intervals
- add laws tests
- add laws to the documentation??
- canonical - add a method to convert to [) interval

- release the library


- short interval are rendered as **, should be [] or (], ...
     * {{{
     *   [***]                                  | [0,10]  : a
     *    [********************]                | [3,50]  : b
     *            [***]                         | [20,30] : c
     *                             [****]       | [60,70] : d
     *                                  [***]   | [71,80] : e
     *   **                                     | [0,3]   : s0
     *    [**]                                  | [3,10]  : s1
     *       [****]                             | [10,20] : s2
     *            [***]                         | [20,30] : s3
     *                [********]                | [30,50] : s4
     *                         [***]            | [50,60] : s5
     *                             [****]       | [60,70] : s6
     *                                  *       | [70,71] : s7
     *                                  [***]   | [71,80] : s8
     * --++--+----+---+--------+---+----+---+-- |
     *   0  10   20  30       50  60   70  80   |
     * }}}

/*
        //
        import com.github.gchudnov.mtg.Diagram.Canvas
        import com.github.gchudnov.mtg.Diagram.View
        import com.github.gchudnov.mtg.Diagram

        val canvas: Canvas  = Canvas.make(40, 2)
        val view: View[Int] = View.default[Int]
        val diagram = Diagram.make(List(a, b, c, d, e, g1, g2), view, canvas)

        val diag = Diagram.render(diagram)

        println(diag)
        //
 */
