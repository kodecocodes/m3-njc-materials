/*
 * Copyright (c) 2024 Kodeco LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * This project and source code may use libraries or frameworks that are
 * released under various Open-Source licenses. Use of those libraries and
 * frameworks are governed by their own individual licenses.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.kodeco.njc.navigationcomponent

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.kodeco.njc.navigationcomponent.screens.BookingConfirmationScreen
import com.kodeco.njc.navigationcomponent.screens.MOVIE_NAME_ARG
import com.kodeco.njc.navigationcomponent.screens.MovieSelectionScreen
import com.kodeco.njc.navigationcomponent.screens.Screens.BOOKING_CONFIRMATION_SCREEN
import com.kodeco.njc.navigationcomponent.screens.Screens.MOVIE_SELECTION_SCREEN
import com.kodeco.njc.navigationcomponent.screens.Screens.TICKET_SELECTION_SCREEN
import com.kodeco.njc.navigationcomponent.screens.Screens.WELCOME_SCREEN
import com.kodeco.njc.navigationcomponent.screens.TICKET_COUNT_ARG
import com.kodeco.njc.navigationcomponent.screens.TicketSelectionScreen
import com.kodeco.njc.navigationcomponent.screens.WelcomeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            var selectedNavBarIndex by remember { mutableIntStateOf(0) }
            Scaffold(
                bottomBar = {
                    NavigationBar {
                        NavigationBarItem(
                            selected = selectedNavBarIndex == 0,
                            label = { Text(text = "Home") },
                            onClick = {
                                navController.navigate(WELCOME_SCREEN.route)
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selectedNavBarIndex == 0) Icons.Filled.Home else Icons.Outlined.Home,
                                    contentDescription = "Home"
                                )
                            }
                        )

                        NavigationBarItem(
                            selected = selectedNavBarIndex == 1,
                            label = { Text(text = "Book Movie") },
                            onClick = {
                                navController.navigate(MOVIE_SELECTION_SCREEN.route)
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selectedNavBarIndex == 1) Icons.Filled.Add else Icons.Outlined.Add,
                                    contentDescription = "Book"
                                )
                            }
                        )
                    }
                }
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = WELCOME_SCREEN.route,
                    modifier = Modifier.padding(innerPadding)
                ) {

                    composable(WELCOME_SCREEN.route) {
                        selectedNavBarIndex = 0
                        WelcomeScreen(onStartClick = { navController.navigate(MOVIE_SELECTION_SCREEN.route) })
                    }
                    composable(
                        MOVIE_SELECTION_SCREEN.route,
                        deepLinks = listOf(navDeepLink {
                            uriPattern = "https://njc-sample-host.kodeco.com/{$MOVIE_NAME_ARG}"
                            action = Intent.ACTION_VIEW
                        })
                    ) { backStackEntry ->
                        selectedNavBarIndex = 1

                        MovieSelectionScreen(
                            onNextClick = { movieName ->
                                navController.navigate(
                                    TICKET_SELECTION_SCREEN.route.replace(
                                        "{$MOVIE_NAME_ARG}",
                                        movieName
                                    )
                                )
                            },
                            movieNameFromDeeplink = backStackEntry.arguments?.getString(
                                MOVIE_NAME_ARG
                            )
                        )
                    }
                    composable(
                        TICKET_SELECTION_SCREEN.route,
                        arguments = listOf(navArgument(MOVIE_NAME_ARG) {
                            type = NavType.StringType
                        })
                    ) { backStackEntry ->
                        TicketSelectionScreen(
                            movieName = requireNotNull(
                                backStackEntry.arguments?.getString(MOVIE_NAME_ARG)
                            ),
                            onNextClick = { movieName, ticketCount ->
                                navController.navigate(
                                    BOOKING_CONFIRMATION_SCREEN.route
                                        .replace("{$MOVIE_NAME_ARG}", movieName)
                                        .replace("{$TICKET_COUNT_ARG}", ticketCount.toString())
                                )
                            }
                        )
                    }
                    composable(
                        BOOKING_CONFIRMATION_SCREEN.route,
                        arguments = listOf(
                            navArgument(MOVIE_NAME_ARG) { type = NavType.StringType },
                            navArgument(TICKET_COUNT_ARG) { type = NavType.IntType }
                        )
                    ) { backStackEntry ->
                        BookingConfirmationScreen(
                            movieName = requireNotNull(
                                backStackEntry.arguments?.getString(MOVIE_NAME_ARG)
                            ),
                            ticketCount = requireNotNull(
                                backStackEntry.arguments?.getInt(TICKET_COUNT_ARG)
                            ),
                        )
                    }
                }
            }

        }
    }
}