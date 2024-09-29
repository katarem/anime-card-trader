package io.github.katarem.presentation.routing

import io.github.katarem.domain.repository.card.CardRepositoryImpl
import io.github.katarem.domain.repository.character.CharacterRepositoryImpl
import io.github.katarem.domain.repository.notification.NotificationRepositoryImpl
import io.github.katarem.domain.repository.trade.TradeRepositoryImpl
import io.github.katarem.domain.repository.user.UserRepositoryImpl
import io.github.katarem.service.notification.NotificationServiceImpl
import io.github.katarem.service.services.auth.AuthService
import io.github.katarem.service.services.cards.CardServiceImpl
import io.github.katarem.service.services.character.CharacterServiceImpl
import io.github.katarem.service.services.resource.ResourceService
import io.github.katarem.service.services.trade.TradeServiceImpl
import io.github.katarem.service.services.user.UserServiceImpl
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configureRouting() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause" , status = HttpStatusCode.InternalServerError)
        }
    }

    //repositories
    val characterRepository = CharacterRepositoryImpl()
    val cardRepository = CardRepositoryImpl()
    val userRepository = UserRepositoryImpl()
    val tradeRepository = TradeRepositoryImpl()
    val notificationRepository = NotificationRepositoryImpl()

    // services
    val characterService = CharacterServiceImpl(characterRepository)
    val userService = UserServiceImpl(userRepository)
    val authService = AuthService(userService)
    val cardService = CardServiceImpl(cardRepository, characterService, userService)
    val notificationService = NotificationServiceImpl(notificationRepository)
    val tradeService = TradeServiceImpl(tradeRepository, cardService)
    val resourceService = ResourceService()

    // Routes
    characterRoute(characterService, authService)
    cardsRoutes(cardService, authService)
    userRoutes(userService, authService)
    notificationRoutes(notificationService, userService, authService)
    tradingRoutes(tradeService, authService)
    authRoutes(userService)
    resourceRoutes(resourceService)

}
