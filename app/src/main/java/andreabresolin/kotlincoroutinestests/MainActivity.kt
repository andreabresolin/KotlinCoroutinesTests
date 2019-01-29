/*
 *  Copyright 2018 Andrea Bresolin
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package andreabresolin.kotlincoroutinestests

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicInteger

class MainActivity : AppCompatActivity() {

    companion object {
        private const val KOTLIN_COROUTINES_TESTS_TAG = "KCT"
        private const val TEST_ITERATIONS_COUNT = 10000
    }

    private var counter = AtomicInteger()
    private var testStartTime: Long = 0
    private val testArray = IntArray(TEST_ITERATIONS_COUNT)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupListeners()
    }

    private fun setupListeners() {
        testCoroutinesButton.setOnClickListener { onTestCoroutinesButtonClick() }
        testAsyncsInCoroutineButton.setOnClickListener { onTestAsyncsInCoroutineButtonClick() }
        testRxJavaButton.setOnClickListener { onTestRxJavaButtonClick() }
    }

    private fun onTestCoroutinesButtonClick() {
        val testName = "Coroutines test"

        startTest(testName)

        for (i in 1..TEST_ITERATIONS_COUNT) {

//            launch(UI) {
//                async(CommonPool) { stubAsyncFunc() }.await()
//                checkTestEnd(testName)
//            }
        }
    }

    private fun onTestAsyncsInCoroutineButtonClick() {
        val testName = "Asyncs in coroutine test"

        startTest(testName)

        GlobalScope.launch {
            testArray
                    .map { async { stubAsyncFunc() } }
                    .map {
                        it.await()
                        checkTestEnd(testName)
                    }
        }


//        launch(UI) {
//        }
    }

    private fun onTestRxJavaButtonClick() {
        val testName = "RxJava test"

        startTest(testName)

        val subscribeScheduler = Schedulers.computation()
        val observeScheduler = AndroidSchedulers.mainThread()

          observableStubAsync().subscribeOn(subscribeScheduler).observeOn(observeScheduler).subscribe {

          }
    }

    private fun stubAsyncFunc() {
        counter.incrementAndGet()
    }

    private fun startTest(testName: String) {
        testStartTime = System.currentTimeMillis()
        logStart(testName)
    }

    private fun checkTestEnd(testName: String) {

        Log.i("testing ending", " -- checking -- ")

        counter.getAndUpdate {
            if (it == TEST_ITERATIONS_COUNT) {
                val testTime = System.currentTimeMillis() - testStartTime

                logEnd("$testName - ${testTime}ms")

                return@getAndUpdate 0
            }

            return@getAndUpdate it
        }
    }

    private fun observableStubAsync(): Observable<Int> {
        return Observable.create {

            for (i in 1..TEST_ITERATIONS_COUNT) {
                counter.incrementAndGet()
            }

            checkTestEnd("rxTest")

        }
    }

    private fun logStart(message: String) {
        Log.i(KOTLIN_COROUTINES_TESTS_TAG, "Start: $message")
    }

    private fun logEnd(message: String) {
        Log.i(KOTLIN_COROUTINES_TESTS_TAG, "End: $message")
    }
}
