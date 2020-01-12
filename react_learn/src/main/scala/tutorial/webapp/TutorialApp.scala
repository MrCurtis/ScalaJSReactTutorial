package tutorial.webapp

package object webapp {
  type Squares = List[Option[String]]
}
import webapp._

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom


object Square {
  case class Props(value:String, onClick:Callback)

  val component = ScalaComponent.builder[Props]("Square")
    .render_P(props =>
      <.button(
        ^.cls := "square",
        ^.onClick --> props.onClick,
        props.value
      )
    )
    .build

    def apply(value: Option[String], onClick: Callback) = component(
      Props(
        value.getOrElse(""),
        onClick
      )
    )
}


object Board {

  case class Props(
    squares: Squares,
    xIsNext: Boolean,
    handleClick: Int => Callback,
  )

  class Backend(bs: BackendScope[Props, Unit]) {

    def render(props: Props) = {
      <.div(
        <.div(
          ^.cls := "board-row",
          renderSquare(props, 0),
          renderSquare(props, 1),
          renderSquare(props, 2)
        ),
        <.div(
          ^.cls := "board-row",
          renderSquare(props, 3),
          renderSquare(props, 4),
          renderSquare(props, 5)
        ),
        <.div(
          ^.cls := "board-row",
          renderSquare(props, 6),
          renderSquare(props, 7),
          renderSquare(props, 8)
        )
      )
    }

    def renderSquare(props: Props, i: Int) = {
      Square(
        props.squares(i),
        props.handleClick(i),
      )
    }
  }

  val component = ScalaComponent.builder[Props]("Board")
    .renderBackend[Backend]
    .build

  def apply(squares: Squares, xIsNext: Boolean, handleClick: Int => Callback)
    = component(Props(squares, xIsNext, handleClick))
}


object Game {

  case class State(
    history: List[Squares],
    stepNumber: Int,
    xIsNext: Boolean,
  )

  class Backend(bs: BackendScope[Unit, State]) {

    def render(state: State) = {
      val current = state.history(state.stepNumber)
      val status = calculateWinner(current) match {
        case Some(s) => "Winner is " + s
        case None => "Next player: " + {if (state.xIsNext) "X" else "O"}
      }
      val moves = state.history.slice(0, state.stepNumber).zipWithIndex.map{
        case (step, move) => {
          val desc = if (move > 0) "Go to move #%s".format(move) else "Go to game start"
          <.li(
            ^.key := move,
            <.button(
              ^.onClick -->  jumpTo(move),
              desc
            )
          )
        }
      }
      <.div(
        ^.cls := "game",
        <.div(
          ^.cls := "game-board",
          Board(current, state.xIsNext, handleClick(_)),
        ),
        <.div(
          ^.cls := "game-info",
          <.div(status),
          <.ol(moves.toTagMod),
        ),
      )
    }

    def calculateWinner(squares: Squares) = {
      val lines = List(
        List(0,1,2),
        List(3,4,5),
        List(6,7,8),
        List(0,3,6),
        List(1,4,7),
        List(2,5,8),
        List(0,4,8),
        List(6,4,2),
      )
      val winner = lines.map(
        line => line.map(
          element => squares(element)
        )
      ).filter(
        _.toSet.size == 1
      ).filter(
        ! _(0).isEmpty
      )
      if (!winner.isEmpty) winner(0)(0) else None
    }

    def handleClick(i: Int) = bs.modState(
      s => {
        val history = s.history.slice(0, s.stepNumber+1)
        val current = history.last
        if (!current(i).isEmpty || !calculateWinner(current).isEmpty) {
          s
        } else {
          s.copy(
            history=history:+current.updated(i, Some(if (s.xIsNext) "X" else "O")),
            xIsNext= !s.xIsNext,
            stepNumber=history.size
          )
        }
      }
    )

    def jumpTo(step: Int) = bs.modState(
      s => s.copy(
        stepNumber=step,
        xIsNext=step % 2 == 0,
      )
    )
  }

  val component = ScalaComponent.builder[Unit]("Board")
    .initialState(State(List(List.fill(9)(None)), 0, true))
    .renderBackend[Backend]
    .build

  def apply() = component()
}


object TutorialApp {
  def main(args: Array[String]): Unit = {
    Game().renderIntoDOM(dom.document.getElementById("root"))
  }

}
