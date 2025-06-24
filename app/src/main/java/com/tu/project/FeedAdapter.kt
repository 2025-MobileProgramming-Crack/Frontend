import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tu.project.FeedPost
import com.tu.project.databinding.ItemFeedPostBinding
import com.bumptech.glide.Glide
import android.view.LayoutInflater


class FeedAdapter(private val postList: List<FeedPost>) :
    RecyclerView.Adapter<FeedAdapter.FeedViewHolder>() {

    inner class FeedViewHolder(val binding: ItemFeedPostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(post: FeedPost) {
            binding.tvUserName.text = "@${post.userName}"
            binding.tvTitle.text = post.title
            binding.tvLikeCount.text = "❤️ ${post.likeCount}"

            Glide.with(binding.root.context)
                .load(post.imageUrl)
                .into(binding.ivImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val binding = ItemFeedPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FeedViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        holder.bind(postList[position])
    }

    override fun getItemCount(): Int = postList.size
}