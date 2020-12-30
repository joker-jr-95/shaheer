import com.aait.shaheer.data_layer.remote.*
import com.kareem.datalayer.remote.*
import retrofit2.Retrofit


private const val BASE_URL = "https://shaheer.4hoste.com/api/"


val authGateway by lazy { retrofit.create(AuthApi::class.java) }
val introGateway by lazy { retrofit.create(IntroApi::class.java) }
val homeGateway by lazy { retrofit.create(HomeApi::class.java) }
val postGateWay by lazy { retrofit.create(PostsApi::class.java) }
val profileGateway by lazy { retrofit.create(ProfileApi::class.java) }
val orderGateway by lazy { retrofit.create(OrderApi::class.java) }
val cartGateway by lazy { retrofit.create(CartApi::class.java) }
val menuGateWay by lazy { retrofit.create(MenuApi::class.java) }
val inboxGateWay by lazy { retrofit.create(InboxApi::class.java) }


private val retrofit: Retrofit = createNetworkClient(BASE_URL, true)

