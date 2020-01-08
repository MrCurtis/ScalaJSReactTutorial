package tutorial.webapp

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

  case class State(
    square:List[Option[String]],
    xIsNext:Boolean
  )

  class Backend(bs: BackendScope[Unit, State]) {

    def render(state: State) = {
      val status = "Next player: " + {if (state.xIsNext) "X" else "O"}
      <.div(
        <.div(
          ^.cls := "status",
          status
        ),
        <.div(
          ^.cls := "board-row",
          renderSquare(state, 0),
          renderSquare(state, 1),
          renderSquare(state, 2)
        ),
        <.div(
          ^.cls := "board-row",
          renderSquare(state, 3),
          renderSquare(state, 4),
          renderSquare(state, 5)
        ),
        <.div(
          ^.cls := "board-row",
          renderSquare(state, 6),
          renderSquare(state, 7),
          renderSquare(state, 8)
        )
      )
    }

    def renderSquare(state: State, i: Int) = {
      Square(
        state.square(i),
        handleClick(i)
      )
    }

    def handleClick(i: Int) = bs.modState(
      s => s.copy(
        square=s.square.updated(i, Some(if (s.xIsNext) "X" else "O")),
        xIsNext= !s.xIsNext
      )
    )
  }

  val component = ScalaComponent.builder[Unit]("Board")
    .initialState(State(List.fill(9)(None), true))
    .renderBackend[Backend]
    .build

  def apply() = component()
}


object Game {
  val component = ScalaComponent.builder[Unit]("Game")
    .renderStatic(
      <.div(
        ^.cls := "game",
        <.div(
          ^.cls := "game-board",
          Board(),
        ),
        <.div(
          <.div(/*Status*/),
          <.ol(/* TODO */),
        ),
      )
    )
    .build

  def apply() = component()
}


object TutorialApp {
  def main(args: Array[String]): Unit = {
    Game().renderIntoDOM(dom.document.getElementById("root"))
  }

}
