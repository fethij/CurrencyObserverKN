import kotlinx.cinterop.*
import platform.Foundation.*
import platform.UIKit.*
import platform.darwin.NSInteger


@ExportObjCClass
class CryptocurrencyTableViewController(aDecoder: NSCoder) : UITableViewController(aDecoder) {

    override fun initWithCoder(aDecoder: NSCoder) = initBy(CryptocurrencyTableViewController(aDecoder))

    private val data = ArrayList<ResponseItem>()


    override fun tableView(tableView: UITableView, cellForRowAtIndexPath: NSIndexPath): UITableViewCell {
        val cell = tableView.dequeueReusableCellWithIdentifier(CELL_IDENTIFIER, cellForRowAtIndexPath) as CryptocurrencyCell
        val item = data.get(cellForRowAtIndexPath.item().toInt())
        val text = item.symbol +item.name + "$" + item.priceUsd.toString()
        val prices = "in 1 hour" + item.percentChange1h

        cell.titleText.setText(text)
        cell.priceText.setText(prices)

//        cell.iconImage.setAutoresizingMask(UIViewAutoresizing.None)
        cell.iconImage.setClipsToBounds(true)

//        UIImageViewExt.sd_setImageWithURLPlaceholderImage(cell.iconImage(), item.getImageUrl().toNSURL(),  UIImage.imageNamed("Cryptocurrency"))
        return cell
    }


    override fun tableView(tableView: UITableView, numberOfRowsInSection: NSInteger): NSInteger {
        println("CurrencyFragment " + "data.size.toLong() = " + data.size.toLong())
        return data.size.toLong()
    }

    override fun numberOfSectionsInTableView(tableView: UITableView): NSInteger {
        return 1
//        return super.numberOfSectionsInTableView(tableView)
    }


    override fun viewDidLoad() {
        super.viewDidLoad()
        println("viewDidLoad")
        doRequest()
    }


    fun doRequest() = runBlocking {
        val apiService = RestService.getCryptoCurrencyService()
        val result = apiService.getCryptocurrency().await()
        updateData(result)
    }

    protected fun updateData(result: List<ResponseItem>) {
        println("CurrencyFragment " + "updateData = " + result)
        data.clear()
        data.addAll(result)
        Globals.dispatch_async(Globals.dispatch_get_main_queue(), {
            this.tableView().reloadData()
        })
//        NSOperationQueue.mainQueue().addOperationWithBlock {
//            tableView().reloadData()
//        }
    }


    companion object {

        const val CELL_IDENTIFIER = "cryptocurrencyCell"

    }
}