package com.adaptionsoft.games.uglytrivia

import java.util.ArrayList
import java.util.LinkedList

enum class Category {
    Pop, Rock, Science, Sports
}

class Game {
    private var players = ArrayList<Any>()
    private var places = IntArray(6)
    private var purses = IntArray(6)
    private var inPenaltyBox = BooleanArray(6)

    private var popQuestions = LinkedList<Any>()
    private var scienceQuestions = LinkedList<Any>()
    private var sportsQuestions = LinkedList<Any>()
    private var rockQuestions = LinkedList<Any>()

    private var currentPlayer = 0
    private var isGettingOutOfPenaltyBox: Boolean = false

    init {
        for (i in 0..49) {
            popQuestions.addLast("Pop Question $i")
            scienceQuestions.addLast("Science Question $i")
            sportsQuestions.addLast("Sports Question $i")
            rockQuestions.addLast(createRockQuestion(i))
        }
    }

    private fun createRockQuestion(index: Int): String = "Rock Question $index"

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
                println("The category is " + currentCategory())
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
            println("The category is " + currentCategory())
            askQuestion()
        }

    }

    private fun askQuestion() {
        if (currentCategory() == Category.Pop)
            println(popQuestions.removeFirst())
        if (currentCategory() == Category.Science)
            println(scienceQuestions.removeFirst())
        if (currentCategory() == Category.Sports)
            println(sportsQuestions.removeFirst())
        if (currentCategory() == Category.Rock)
            println(rockQuestions.removeFirst())
    }

    private fun currentCategory(): Category =
            when (places[currentPlayer]) {
                in arrayOf(0, 4, 8) -> Category.Pop
                in arrayOf(1, 5, 9) -> Category.Science
                in arrayOf(2, 6, 10) -> Category.Sports
                else -> Category.Rock
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