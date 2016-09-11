package com.haosu.schedulebook;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.haosu.schedulebook.db.XUtil;
import com.haosu.schedulebook.listeners.OnMoveAndSwipedListener;
import com.haosu.schedulebook.model.ScheduleItem;
import com.haosu.schedulebook.util.DateUtil;
import com.xiaomi.mistatistic.sdk.MiStatInterface;

import org.xutils.DbManager;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ItemTouchHelper itemTouchHelper;
    private RecyclerView recyclerView;
    private ScheduleAdapter adapter;
    private List<ScheduleItem> dataList = new ArrayList<>();

    private Snackbar snackbar;
    private int lastIndex = -1;
    private ScheduleItem lastItem = null;

    private boolean isTody = true;

    private void toggleDate() {
        isTody = !isTody;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.today);
        toolbar.setSubtitle(DateUtil.simpleFormat());
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, CreateSchedulItemActivity.class);
                startActivity(intent);
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.main_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ScheduleAdapter(dataList);
        recyclerView.setAdapter(adapter);

        itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.END) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                adapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                adapter.refreshOrder();
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                adapter.onItemDismiss(viewHolder.getAdapterPosition());
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        snackbar = Snackbar.make(findViewById(R.id.coordinator_layout), R.string.snack_bar_text, Snackbar.LENGTH_LONG).setAction(R.string.snack_bar_button, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastIndex != -1 && lastItem != null && adapter != null) {
                    lastItem.setId(0);
                    adapter.add(lastItem, lastIndex);
                    adapter.refreshOrder();
                    adapter.notifyDataSetChanged();
                }
            }
        });

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleDate();
                if (isTody) {
                    Log.i(MainActivity.class.getSimpleName(),"date toggle, is today, show today's schedules and date");
                    new LoadItemsTask(adapter).execute();
                    toolbar.setTitle(R.string.today);
                    toolbar.setSubtitle(DateUtil.simpleFormat());
                } else {
                    Log.i(MainActivity.class.getSimpleName(),"date toggle, is tomorrow, show tomorrow's schedules and date");
                    new LoadItemsTask(adapter).execute(DateUtil.simpleFormatDateNearToday(1));
                    toolbar.setTitle(R.string.tomorrow);
                    toolbar.setSubtitle(DateUtil.simpleFormatDateNearToday(1));
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        new LoadItemsTask(adapter).execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MiStatInterface.recordPageStart(this, this.getClass().getSimpleName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        adapter.refreshOrder();
        MiStatInterface.recordPageEnd();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_about) {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> implements OnMoveAndSwipedListener {

        private List<ScheduleItem> list;
        private Set<Integer> idSet;

        public ScheduleAdapter(List<ScheduleItem> list) {
            this.list = list;
            idSet = new HashSet<>();
        }

        public void clear() {
            list.clear();
            idSet.clear();
        }

        public void add(ScheduleItem item) {
            if (!idSet.contains(item.getId())) {
                idSet.add(item.getId());
                list.add(item);
            } else {
                for (ScheduleItem i : list) {
                    if (i.getId() == item.getId()) {
                        i.setText(item.getText());
                        i.setDate(item.getDate());
                        break;
                    }
                }
            }
        }

        public void add(ScheduleItem item, int position) {
            if (!idSet.contains(item.getId())) {
                idSet.add(item.getId());
                list.add(position, item);
            } else {
                for (ScheduleItem i : list) {
                    if (i.getId() == item.getId()) {
                        i.setText(item.getText());
                        i.setDate(item.getDate());
                        break;
                    }
                }
            }
        }

        @Override
        public ScheduleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(
                    MainActivity.this).inflate(R.layout.schedule_item_layout, parent,
                    false);
            ScheduleViewHolder holder = new ScheduleViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final ScheduleViewHolder holder, final int position) {
            holder.textView.setText(list.get(position).getText());
            holder.checkBox.setChecked(list.get(position).isFinish());
            holder.itemView.setClickable(true);
//            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//                    ScheduleItem item = list.get(holder.getAdapterPosition());
//                    Intent intent = new Intent(MainActivity.this, CreateSchedulItemActivity.class);
//                    intent.putExtra("item", item);
//                    startActivity(intent);
//                    return true;
//                }
//            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        @Override
        public boolean onItemMove(int fromPosition, int toPosition) {
            Collections.swap(list, fromPosition, toPosition);
            notifyItemMoved(fromPosition, toPosition);
            return true;
        }

        public void refreshOrder() {
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setMyorder(i);
                try {
                    DbManager db = x.getDb(XUtil.getDaoConfig());
                    db.saveOrUpdate(list.get(i));
                } catch (Exception e) {
                    Log.e("DB_OPERATION", e.getMessage());
                }
            }
        }

        @Override
        public void onItemDismiss(final int position) {
            if (position < list.size()) {
                ScheduleItem item = list.get(position);
                idSet.remove(item.getId());
                list.remove(position);
                try {
                    DbManager db = x.getDb(XUtil.getDaoConfig());
                    db.delete(item);
                    this.notifyDataSetChanged();
                } catch (Exception e) {
                    Log.e("DB_OPERATION", e.getMessage());
                }
                lastIndex = position;
                lastItem = item;
                if (snackbar != null) {
                    snackbar.show();
                }
            }
        }

        class ScheduleViewHolder extends RecyclerView.ViewHolder {

            CheckBox checkBox;
            TextView textView;

            public ScheduleViewHolder(View itemView) {
                super(itemView);
                checkBox = (CheckBox) itemView.findViewById(R.id.schedule_item_check);
                textView = (TextView) itemView.findViewById(R.id.schedule_item_text);
                checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ScheduleItem item = list.get(getAdapterPosition());
                        item.setFinish(checkBox.isChecked());
                        try {
                            DbManager.DaoConfig daoConfig = XUtil.getDaoConfig();
                            DbManager db = x.getDb(daoConfig);
                            db.saveOrUpdate(item);
                        } catch (Exception e) {
                            Log.e(this.getClass().getSimpleName(), e.getMessage(), e);
                        }
                    }
                });
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ScheduleItem item = list.get(getAdapterPosition());
                        checkBox.toggle();
                        item.setFinish(checkBox.isChecked());
                        try {
                            DbManager.DaoConfig daoConfig = XUtil.getDaoConfig();
                            DbManager db = x.getDb(daoConfig);
                            db.saveOrUpdate(item);
                        } catch (Exception e) {
                            Log.e(this.getClass().getSimpleName(), e.getMessage(), e);
                        }
                    }
                });
            }
        }
    }

    class LoadItemsTask extends AsyncTask<String, Void, Void> {

        ScheduleAdapter adapter;

        public LoadItemsTask(ScheduleAdapter adapter) {
            this.adapter = adapter;
        }

        @Override
        protected Void doInBackground(String... params) {
            this.adapter.clear();
            String date = null;
            if (params.length == 1) {
                date = params[0];
            } else {
                date = DateUtil.simpleFormat();
            }
            try {
                DbManager db = x.getDb(XUtil.getDaoConfig());
                List<ScheduleItem> list = db.selector(ScheduleItem.class).where("date", "=", date).orderBy("myorder").findAll();
                if (list != null) {
                    for (ScheduleItem i : list) {
                        adapter.add(i);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter.notifyDataSetChanged();
            adapter.refreshOrder();
        }
    }

}
