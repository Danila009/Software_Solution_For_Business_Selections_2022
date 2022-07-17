package common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

fun <T>Flow<T>.dispatchersIO(){
    CoroutineScope(Dispatchers.IO).launch {
        this@dispatchersIO.collect()
    }
}