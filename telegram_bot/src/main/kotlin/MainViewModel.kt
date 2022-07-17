import api.Api
import api.model.Employee
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel {

    private val api = Api()

    private val _responseEmployees = MutableStateFlow(emptyList<Employee>())
    val responseEmployees = _responseEmployees.asStateFlow()

    private val _responseEmployeesCheckById = MutableStateFlow<Boolean?>(null)
    val responseEmployeesCheckById = _responseEmployeesCheckById.asStateFlow()

    fun getAllEmployees(
        search:String = "",
        positionId:Int? = null,
        postId:Int? = null
    ){
        CoroutineScope(Dispatchers.IO).launch {
            val response = api.getAllEmployees(
                search, positionId, postId
            )
            _responseEmployees.value = response
        }
    }

    fun getEmployeesCheckById(fio:String){
        CoroutineScope(Dispatchers.IO).launch {
            val response = api.getEmployeesCheckById(fio)
            _responseEmployeesCheckById.value = response
        }
    }
}