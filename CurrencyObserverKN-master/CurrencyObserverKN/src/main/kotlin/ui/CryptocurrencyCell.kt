import kotlinx.cinterop.ExportObjCClass
import kotlinx.cinterop.ObjCOutlet
import kotlinx.cinterop.initBy
import platform.Foundation.NSCoder
import platform.UIKit.UIImageView
import platform.UIKit.UITableViewCell
import platform.UIKit.UITextView


@ExportObjCClass
class CryptocurrencyCell(aDecoder: NSCoder) : UITableViewCell(aDecoder) {

    override fun initWithCoder(aDecoder: NSCoder) = initBy(CryptocurrencyCell(aDecoder))


    @ObjCOutlet
    lateinit var iconImage: UIImageView

    @ObjCOutlet
    lateinit var titleText: UITextView

    @ObjCOutlet
    lateinit var priceText: UITextView



}