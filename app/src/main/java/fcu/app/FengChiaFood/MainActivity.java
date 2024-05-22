package fcu.app.FengChiaFood;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView store_list;
    private List<ShopDetails> shopDetailsList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        store_list = findViewById(R.id.StoreList);
        store_list.setLayoutManager(new LinearLayoutManager(this));
        shopDetailsList = new ArrayList<>();
        shopDetailsList.add(new ShopDetails(R.drawable.menmonster, "Men Monster", "4.4"));
        shopDetailsList.add(new ShopDetails(R.drawable.hun2, "HUN 貳", "4.5"));
        shopDetailsList.add(new ShopDetails(R.drawable.dawang, "大王麻辣乾麵-逢甲旗艦店", "4.7"));
        shopDetailsList.add(new ShopDetails(R.drawable.dapu, "大埔鐵板燒 河南逢甲店", "3.9"));
        shopDetailsList.add(new ShopDetails(R.drawable.zhengxian, "爭鮮迴轉壽司-逢甲店", "4.0"));

        StoreListAdapter adapter = new StoreListAdapter(this, shopDetailsList);
        store_list.setAdapter(adapter);
    }
}