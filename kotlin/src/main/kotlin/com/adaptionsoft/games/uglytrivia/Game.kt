package com.adaptionsoft.games.uglytrivia

import java.util.ArrayList
import java.util.Deque
import java.util.EnumMap
import java.util.LinkedList

enum class Category {
    Pop, Science, Sports, Rock
}

class Game {
    companion object {
        private const val NB_CELLS = 12
    }

    private val categories: Array<Category>
        get() = Category.values()
    private val categoriesByPosition: MutableMap<Int, Category> = HashMap(NB_CELLS)
    private val questionsByCategory: EnumMap<Category, Deque<String>> = EnumMap(Category::class.java)

    private var players = ArrayList<Any>()
    private var places = IntArray(6)
    private var purses = IntArray(6)
    private var inPenaltyBox = BooleanArray(6)

    private val currentPosition: Int
        get() = places[currentPlayer]

    private val currentCategory: Category
        get() = categoriesByPosition[currentPosition]
                ?: Category.Rock

    private val numberPlayers: Int
        get() = players.size

    private var currentPlayer = 0
    private var isGettingOutOfPenaltyBox: Boolean = false

    init {
        for (category in categories) {
            questionsByCategory[category] = LinkedList<String>()
        }
        for (i in 0 until 50) {
            for (category in categories) {
                questionsByCategory[category]?.addLast("$category Question $i")
            }
        }
        for (i in 0 until NB_CELLS) {
            categoriesByPosition[i] = categories[i % categories.size]
        }
    }

    fun add(playerName: String) {
        players.add(playerName)
        places[numberPlayers] = 0
        purses[numberPlayers] = 0
        inPenaltyBox[numberPlayers] = false

        println("$playerName was added")
        println("There are $numberPlayers players")
    }


    fun roll(roll: Int) {
        println("${players[currentPlayer]} is the current player")
        println("They have rolled a $roll")

        if (inPenaltyBox[currentPlayer]) {
            if (roll % 2 != 0) {
                isGettingOutOfPenaltyBox = true
                println("${players[currentPlayer]} is getting out of the penalty box")
                move(roll)
                println("${players[currentPlayer]}'s new location is $currentPosition")
                println("The category is $currentCategory")
                askQuestion()
            } else {
                println("${players[currentPlayer]} is not getting out of the penalty box")
                isGettingOutOfPenaltyBox = false
            }
        } else {
            move(roll)
            println("${players[currentPlayer]}'s new location is $currentPosition")
            println("The category is $currentCategory")
            askQuestion()
        }
    }

    private fun move(offset: Int) {
        places[currentPlayer] = (currentPosition + offset) % NB_CELLS
    }

    private fun askQuestion() {
        println(questionsByCategory[currentCategory]?.removeFirst())
    }

    fun wasCorrectlyAnswered(): Boolean {
        if (inPenaltyBox[currentPlayer]) {
            return if (isGettingOutOfPenaltyBox) {
                println("Answer was correct!!!!")
                purses[currentPlayer]++
                println("${players[currentPlayer]} now has ${purses[currentPlayer]} Gold Coins.")

                val winner = didPlayerWin()
                nextPlayer()

                winner
            } else {
                nextPlayer()
                true
            }
        } else {
            println("Answer was correct!!!!")
            purses[currentPlayer]++
            println("${players[currentPlayer]} now has ${purses[currentPlayer]} Gold Coins.")

            val winner = didPlayerWin()
            nextPlayer()

            return winner
        }
    }

    fun wrongAnswer(): Boolean {
        println("Question was incorrectly answered")
        println("${players[currentPlayer]} was sent to the penalty box")
        inPenaltyBox[currentPlayer] = true

        nextPlayer()
        return true
    }

    private fun nextPlayer() {
        currentPlayer = (currentPlayer + 1) % players.size
    }

    private fun didPlayerWin(): Boolean = purses[currentPlayer] != 6
}