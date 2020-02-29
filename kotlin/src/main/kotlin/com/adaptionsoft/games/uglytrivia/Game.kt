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

    fun add(playerName: String): Boolean {
        players.add(playerName)
        places[howManyPlayers()] = 0
        purses[howManyPlayers()] = 0
        inPenaltyBox[howManyPlayers()] = false

        println("$playerName was added")
        println("They are player number " + players.size)
        return true
    }

    private fun howManyPlayers(): Int = players.size

    fun roll(roll: Int) {
        println(players[currentPlayer].toString() + " is the current player")
        println("They have rolled a $roll")

        if (inPenaltyBox[currentPlayer]) {
            if (roll % 2 != 0) {
                isGettingOutOfPenaltyBox = true

                println(players[currentPlayer].toString() + " is getting out of the penalty box")
                places[currentPlayer] = places[currentPlayer] + roll
                if (places[currentPlayer] > 11) places[currentPlayer] = places[currentPlayer] - 12

                println(players[currentPlayer].toString()
                        + "'s new location is "
                        + places[currentPlayer])
                println("The category is $currentCategory")
                askQuestion()
            } else {
                println(players[currentPlayer].toString() + " is not getting out of the penalty box")
                isGettingOutOfPenaltyBox = false
            }

        } else {

            places[currentPlayer] = places[currentPlayer] + roll
            if (places[currentPlayer] > 11) places[currentPlayer] = places[currentPlayer] - 12

            println(players[currentPlayer].toString()
                    + "'s new location is "
                    + places[currentPlayer])
            println("The category is $currentCategory")
            askQuestion()
        }

    }

    private fun askQuestion() {
        println(questionsByCategory[currentCategory]?.removeFirst())
    }

    fun wasCorrectlyAnswered(): Boolean {
        if (inPenaltyBox[currentPlayer]) {
            if (isGettingOutOfPenaltyBox) {
                println("Answer was correct!!!!")
                purses[currentPlayer]++
                println(players[currentPlayer].toString()
                        + " now has "
                        + purses[currentPlayer]
                        + " Gold Coins.")

                val winner = didPlayerWin()
                currentPlayer++
                if (currentPlayer == players.size) currentPlayer = 0

                return winner
            } else {
                currentPlayer++
                if (currentPlayer == players.size) currentPlayer = 0
                return true
            }


        } else {

            println("Answer was corrent!!!!")
            purses[currentPlayer]++
            println(players[currentPlayer].toString()
                    + " now has "
                    + purses[currentPlayer]
                    + " Gold Coins.")

            val winner = didPlayerWin()
            currentPlayer++
            if (currentPlayer == players.size) currentPlayer = 0

            return winner
        }
    }

    fun wrongAnswer(): Boolean {
        println("Question was incorrectly answered")
        println(players[currentPlayer].toString() + " was sent to the penalty box")
        inPenaltyBox[currentPlayer] = true

        currentPlayer++
        if (currentPlayer == players.size) currentPlayer = 0
        return true
    }

    private fun didPlayerWin(): Boolean = purses[currentPlayer] != 6
}