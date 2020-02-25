package com.adaptionsoft.games.trivia

import com.adaptionsoft.games.uglytrivia.Game
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.util.Random

class GameTest {

    @Test
    fun `should record and verify golden master`() {
        val result = playGame(1L)
        val expected = this::class.java
                .getResource("/expected.txt")
                .readText(Charsets.UTF_8)

        assertEquals(expected, result)
    }

    private fun playGame(seed: Long): String {
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))

        val aGame = Game()
                .apply {
                    add("Chet")
                    add("Pat")
                    add("Sue")
                }
        var notAWinner: Boolean
        val rand = Random(seed)

        do {
            aGame.roll(rand.nextInt(5) + 1)

            notAWinner = if (rand.nextInt(9) == 7) {
                aGame.wrongAnswer()
            } else {
                aGame.wasCorrectlyAnswered()
            }
        } while (notAWinner)

        return String(outputStream.toByteArray())
    }
}