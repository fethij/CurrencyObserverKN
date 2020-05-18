import kotlinx.cinterop.*
import platform.Foundation.*
import platform.UIKit.*
import platform.darwin.NSInteger


@ExportObjCClass
class CurrencyTableViewController(aDecoder: NSCoder) : UITableViewController(aDecoder) {

    override fun initWithCoder(aDecoder: NSCoder) = initBy(CurrencyTableViewController(aDecoder))


    private val data = ArrayList<Pair<String, Double>>()
	
	    override fun tableView(tableView: UITableView, cellForRowAtIndexPath: NSIndexPath): UITableViewCell {
        val cell = tableView.dequeueReusableCellWithIdentifierForIndexPath(CELL_IDENTIFIER, cellForRowAtIndexPath) as UITableViewCell
        val item = data.get(cellForRowAtIndexPath.item().toInt())
        val text = "1 EUR = ${item.second} ${item.first}"
        cell.textLabel().setText(text)
        val url = getImageUrl(item.first)
        println("CurrencyFragment " + "url = " + url)
        UIImageViewExt.sd_setImageWithURLPlaceholderImage(cell.imageView(), url,  UIImage.imageNamed("Currency"))
        return cell
    }


    override fun tableView(tableView: UITableView, numberOfRowsInSection: NSInteger): NSInteger {
        println("CurrencyFragment " + "data.size.toLong() = " + data.size.toLong())
        return data.size.toLong()
    }

    override fun numberOfSectionsInTableView(tableView: UITableView) = 1
	

    override fun viewDidLoad() {
        super.viewDidLoad()
        println("viewDidLoad")
        doRequest()
    }

    private fun getImageUrl(name: String) = NSURL.URLWithString("http://www.xe.com/themes/xe/images/flags/${name.toLowerCase(Locale.US)}.png")

    fun doRequest() = runBlocking {
        val apiService = RestService.getCurrencyService()
        val result = apiService.getCurrency().await()
        println("CurrencyFragment " + "doRequest")
//        NSOperationQueue.mainQueue().addOperationWithBlock {
//            ad(result)
//        }
        ad(result)
    }

    fun ad(result: CurrencyResponse) {
        println("CurrencyFragment " + "Result = " + result)

        val list = result.rates!!.entries.map { Pair(it.key, it.value) }

        updateData(list)
    }

    protected fun updateData(result: List<Pair<String, Double>>) {
        println("CurrencyFragment " + "updateData = " + result)
        data.clear()
        data.addAll(result)
        NSOperationQueue.mainQueue().addOperationWithBlock {
            tableView().reloadData()
        }
    }


    companion object {
        const val CELL_IDENTIFIER = "currencyCell"
    }
}