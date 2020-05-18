import com.github.devjn.currencyobserver.rest.data.CurrencyResponse
import com.github.devjn.currencyobserver.rest.data.ResponseItem
import kotlinx.cinterop.*
import platform.darwin.NSObject
import platform.Foundation.*

class Utils {


    private fun asyncRequest(url: String) {

        val delegate = object : NSObject(), NSURLSessionDataDelegateProtocol {
            val receivedData = NSMutableData()

            override fun URLSession(session: NSURLSession, dataTask: NSURLSessionDataTask, didReceiveData: NSData) {
                receivedData.appendData(didReceiveData)
            }

            override fun URLSession(session: NSURLSession, task: NSURLSessionTask, didCompleteWithError: NSError?) {

                val response = task.response
                if (response == null || response.reinterpret<NSHTTPURLResponse>().statusCode.toInt() != 200) {
                    return
                }

                if (didCompleteWithError != null) {
                    return
                }

                // TODO: report errors.

                val receivedStats = parseJsonResponse(receivedData)
                if (receivedStats != null) {
                    mostRecentFetched = receivedStats
                    receivedStats.reportToGameCenter()
                }
            }
        }

        val session = NSURLSession.sessionWithConfiguration(
                NSURLSessionConfiguration.defaultSessionConfiguration(),
                delegate,
                delegateQueue = NSOperationQueue.mainQueue()
        )

        session.dataTaskWithURL(NSURL(URLString = url)).resume()
    }

    private fun parseJsonResponseItem(data: NSData): ResponseItem? {
        val jsonObject = memScoped {
            val errorVar = alloc<ObjCObjectVar<NSError?>>()
            val result = NSJSONSerialization.JSONObjectWithData(data, 0, errorVar.ptr)
            val error = errorVar.value
            if (error != null) {
                throw Error("JSON parsing error: $error")
            }
            result!!
        }

        val dict = jsonObject.reinterpret<NSDictionary>()

        val myTeamIndex = dict.intValueForKey("color")!! - 1
        val myTeam = Team.values()[myTeamIndex]

        val myConribution = dict.intValueForKey("contribution")!!
        val status = dict.intValueForKey("status")!!
        val winner = dict.intValueForKey("winner")!!

        val colors = dict.valueForKey("colors")!!.reinterpret<NSArray>()
        val counts = IntArray(Team.count)
        assert(colors.count == counts.size.toLong())
        for (i in 0 until colors.count) {
            val element = colors.objectAtIndex(i)!!.reinterpret<NSDictionary>()
            val counter = element.valueForKey("counter")!!.reinterpret<NSNumber>().integerValue().toInt()
            counts[i.toInt()] = counter
        }

        return ResponseItem(counts, myTeam, myConribution, status, winner != 0)
    }

    fun parseJsonCurrencyResponse(data: NSData): CurrencyResponse? {
        val jsonObject = memScoped {
            val errorVar = alloc<ObjCObjectVar<NSError?>>()
            val result = NSJSONSerialization.JSONObjectWithData(data, 0, errorVar.ptr)
            val error = errorVar.value
            if (error != null) {
                throw Error("JSON parsing error: $error")
            }
            result!!
        }

        val dict = jsonObject.reinterpret<NSDictionary>()

        val myTeamIndex = dict.intValueForKey("color")!! - 1
        val myTeam = Team.values()[myTeamIndex]

        val myConribution = dict.intValueForKey("contribution")!!
        val status = dict.intValueForKey("status")!!
        val winner = dict.intValueForKey("winner")!!

        val colors = dict.valueForKey("colors")!!.reinterpret<NSArray>()
        val counts = IntArray(Team.count)
        assert(colors.count == counts.size.toLong())
        for (i in 0 until colors.count) {
            val element = colors.objectAtIndex(i)!!.reinterpret<NSDictionary>()
            val counter = element.valueForKey("counter")!!.reinterpret<NSNumber>().integerValue().toInt()
            counts[i.toInt()] = counter
        }

        return CurrencyResponse(counts, myTeam, myConribution, status, winner != 0)
    }

    private fun NSDictionary.intValueForKey(key: String): Int? =
            this.valueForKey(key)?.reinterpret<NSNumber>()?.integerValue()?.toInt()

}