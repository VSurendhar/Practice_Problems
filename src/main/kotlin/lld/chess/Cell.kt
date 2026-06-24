package lld.chess

import java.util.Stack
import java.util.UUID
import kotlin.collections.ArrayDeque
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.contentDeepEquals
import kotlin.collections.contentDeepHashCode
import kotlin.collections.forEach
import kotlin.collections.map
import kotlin.collections.mutableListOf
import kotlin.collections.toTypedArray

abstract class Coin(
    open val name: String,
)

enum class ChessColor {
    BLACK, WHITE
}

object EmptyCoin : Coin("EmptyCoin")

abstract class MovableCoin(
    override val name: String,
    open val color: ChessColor,
    val isAlive: Boolean = true,
    val playerId: String,
) :
    Coin(name = name) {
    abstract fun canMove(x1: Int, y1: Int, x2: Int, y2: Int): Boolean
}

class Knight(color: ChessColor, playerId: String) : MovableCoin(name = "Knight", color = color, playerId = playerId) {
    override fun canMove(x1: Int, y1: Int, x2: Int, y2: Int): Boolean {
        return true
    }
}

class Rook(color: ChessColor, playerId: String) : MovableCoin(name = "Rook", color = color, playerId = playerId) {
    override fun canMove(x1: Int, y1: Int, x2: Int, y2: Int): Boolean {
        return true
    }
}

class Bishop(color: ChessColor, playerId: String) : MovableCoin(name = "Bishop", color = color, playerId = playerId) {
    override fun canMove(x1: Int, y1: Int, x2: Int, y2: Int): Boolean {
        return true
    }
}

class Pawn(color: ChessColor, playerId: String) : MovableCoin(name = "Pawn", color = color, playerId = playerId) {
    override fun canMove(x1: Int, y1: Int, x2: Int, y2: Int): Boolean {
        return true
    }
}

class King(color: ChessColor, playerId: String) : MovableCoin(name = "King", color = color, playerId = playerId) {
    override fun canMove(x1: Int, y1: Int, x2: Int, y2: Int): Boolean {
        return true
    }
}

class Queen(color: ChessColor, playerId: String) : MovableCoin(name = "Queen", color = color, playerId = playerId) {
    override fun canMove(x1: Int, y1: Int, x2: Int, y2: Int): Boolean {
        return true
    }
}

class Cell(val x: Int, val y: Int, val piece: Coin = EmptyCoin)

fun initialCells(blackPlayerId: String, whitePlayerId: String): Array<Array<Cell>> = Array(8)
{ row ->
    Array(8) { col ->
        val piece = when (row) {
            0 -> when (col) {
                0, 7 -> Rook(color = ChessColor.BLACK, blackPlayerId)
                1, 6 -> Knight(color = ChessColor.BLACK, blackPlayerId)
                2, 5 -> Bishop(color = ChessColor.BLACK, blackPlayerId)
                3 -> Queen(color = ChessColor.BLACK, blackPlayerId)
                4 -> King(color = ChessColor.BLACK, blackPlayerId)
                else -> EmptyCoin
            }

            7 -> when (col) {
                0, 7 -> Rook(color = ChessColor.WHITE, whitePlayerId)
                1, 6 -> Knight(color = ChessColor.WHITE, whitePlayerId)
                2, 5 -> Bishop(color = ChessColor.WHITE, whitePlayerId)
                3 -> Queen(color = ChessColor.WHITE, whitePlayerId)
                4 -> King(color = ChessColor.WHITE, whitePlayerId)
                else -> EmptyCoin
            }

            1 -> Pawn(color = ChessColor.BLACK, blackPlayerId)

            6 -> Pawn(color = ChessColor.WHITE, whitePlayerId)
            else -> EmptyCoin
        }

        Cell(row, col, piece)
    }
}

private const val EMPTY_STRING = ""

data class Board(val cells: Array<Array<Cell>>) : Cloneable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Board

        if (!cells.contentDeepEquals(other.cells)) return false

        return true
    }

    override fun hashCode(): Int {
        return cells.contentDeepHashCode()
    }

    public override fun clone(): Board {
        return Board(cells.map { row -> row.map { cell -> Cell(cell.x, cell.y, cell.piece) }.toTypedArray() }
            .toTypedArray())
    }

}

data class Player(val playerId: String, val playerData: PlayerData)

data class PlayerData(val gameHistoryList: MutableList<GameHistory>)

data class GameHistory(
    val moves: MutableList<ChessMove> = mutableListOf(),
    val winPlayerId: String = EMPTY_STRING,
    val lostPlayerId: String = EMPTY_STRING,
    val isDraw: Boolean = false,
    var startTime: Long = 0,
    var endTime: Long = 0,
)

data class ChessMove(
    val x1: Int,
    val y1: Int,
    val x2: Int,
    val y2: Int,
    val playerId: String,
)

interface MoveValidator {
    fun isValid(
        board: Board,
        move: GameCommand.Move,
    ): MoveResult
}

class DefaultMoveValidator() : MoveValidator {
    override fun isValid(board: Board, move: GameCommand.Move): MoveResult {
        val coin = board.cells[move.x1][move.y1].piece
        val destinationCoin = board.cells[move.x2][move.y2].piece
        if (coin is MovableCoin && (destinationCoin is EmptyCoin || (destinationCoin is MovableCoin && isCapturable(
                coin,
                destinationCoin
            )))
        ) {
            val canMove = coin.canMove(move.x1, move.y1, move.x2, move.y2)
            if (canMove && destinationCoin is MovableCoin && isCapturable(
                    coin,
                    destinationCoin
                )
            ) return MoveResult.CAPTURED
            if (canMove) return MoveResult.VALID
        }
        return MoveResult.INVALID
    }

    private fun isCapturable(currentCoin: MovableCoin, destinationCoin: MovableCoin): Boolean {
        return false
    }
}

enum class MoveResult {
    VALID, INVALID, CHECK, CAPTURED
}

class Game(
    private val gameId: String,
    private var board: Board,
    private val players: List<Player>,
) {

    private val gameHistory: GameHistory = GameHistory()
    private val playerToggler: ArrayDeque<Player> = ArrayDeque<Player>()
    private val undoStack: Stack<GameCommand.Move> = Stack()
    private val redoStack: Stack<GameCommand.Move> = Stack()

    fun startGame() {
        gameHistory.startTime = System.currentTimeMillis()
        refreshBoard()
    }

    private fun refreshBoard() {
        board = Board(initialCells(players[0].playerId, players[1].playerId))
    }

    fun saveGameMove(move: GameCommand.Move) {
        gameHistory.moves.add(
            ChessMove(
                x1 = move.x1,
                y1 = move.y1,
                x2 = move.x2,
                y2 = move.y2,
                playerId = move.playerId,
            )
        )
    }

    fun undoMove() {
        togglePlayer()
    }

    fun redoMove() {
        togglePlayer()
    }

    fun moveGameCoin() {

    }

    fun getCoinAt(row: Int, col: Int): Coin {
        return board.cells[row][col].piece
    }

    fun togglePlayer() {
        val player = playerToggler.removeFirst()
        playerToggler.addLast(player)
    }

    fun getCurrentPlayer(): Player {
        return playerToggler.first()
    }

    fun endGame() {
        gameHistory.endTime = System.currentTimeMillis()
        players.forEach { it.playerData.gameHistoryList.add(gameHistory) }
    }

    fun getBoard(): Board {
        return board.clone()
    }

}

abstract class GameManager(
    val game: Game,
    private val observer: GameStateObserver,
) {

    protected open val moveValidator: MoveValidator = DefaultMoveValidator()

    open fun executeCommand(command: GameCommand) {
        when (command) {

            is GameCommand.Move -> {
                val coin = getCoinAt(command.x1, command.y1)
                if (coin is MovableCoin) {
                    val canMove = moveValidator.isValid(
                        board = game.getBoard(), move = command
                    )
                    if (canMove == MoveResult.VALID || canMove == MoveResult.CAPTURED) {
                        game.moveGameCoin()
                        game.saveGameMove(command)
                        game.togglePlayer()
                        if (canMove == MoveResult.CAPTURED) {
                            val destinationCoin = getCoinAt(command.x2, command.y2) as MovableCoin
                            observer.onGameStateChanged(
                                GameState.LostPiece(
                                    piece = destinationCoin
                                )
                            )
                            return
                        }
                    }
                    observer.onGameStateChanged(GameState.CurrentTurn(game.getCurrentPlayer().playerId))
                } else {
                    observer.onGameStateChanged(
                        GameState.InvalidMove(
                            playerId = command.playerId,
                            x1 = command.x1,
                            y1 = command.y1,
                            x2 = command.x2,
                            y2 = command.y2,
                            coin = getCoinAt(command.x1, command.y1)
                        )
                    )
                }
            }

            is GameCommand.Undo -> {
                game.undoMove()
                observer.onGameStateChanged(GameState.CurrentTurn(game.getCurrentPlayer().playerId))
            }

            is GameCommand.Redo -> {
                game.redoMove()
                observer.onGameStateChanged(GameState.CurrentTurn(game.getCurrentPlayer().playerId))
            }

        }
    }

    fun getCoinAt(row: Int, col: Int): Coin {
        return game.getCoinAt(row, col)
    }
}

class OfflineH2HGameManager(
    private val players: List<Player>,
    private val observer: GameStateObserver,
) : GameManager(
    game = Game(
        gameId = UUID.randomUUID().toString(),
        board = Board(initialCells(players[0].playerId, players[1].playerId)),
        players = players,
    ), observer = observer
) {
    fun startGame() {
        super.game.startGame()
    }
    fun endGame() {
        super.game.endGame()
    }
}

class OfflineH2BGameManager(
    private val players: List<Player>,
    private val observer: GameStateObserver,
) : GameManager(
    game = Game(
        gameId = UUID.randomUUID().toString(),
        board = Board(initialCells(players[0].playerId, players[1].playerId)),
        players = players,
    ), observer = observer
) {
    fun startGame() {
        super.game.startGame()
    }
    fun endGame() {
        super.game.endGame()
    }
}

sealed interface GameCommand {
    class Move(
        val x1: Int,
        val y1: Int,
        val x2: Int,
        val y2: Int,
        val playerId: String,
    ) : GameCommand

    class Undo(private val playerId: String) : GameCommand
    class Redo(private val playerId: String) : GameCommand
}

sealed interface GameState {
    class WIN(playerId: String) : GameState
    class LOST(playerId: String) : GameState
    object DRAW : GameState
    class CurrentTurn(val playerId: String) : GameState
    class LostPiece(val piece: MovableCoin) : GameState
    class InvalidMove(
        val playerId: String, val x1: Int,
        val y1: Int,
        val x2: Int,
        val y2: Int,
        val coin: Coin,
    ) : GameState

    object EmptyGameState : GameState
}

interface GameStateObserver {
    fun onGameStateChanged(gameState: GameState)
}

class UI : GameStateObserver {
    override fun onGameStateChanged(gameState: GameState) {}
}
