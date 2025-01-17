package com.example.ph19127_mob2041;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.ph19127_mob2041.dao.LoaiSachDAO;
import com.example.ph19127_mob2041.dao.PhieuMuonDAO;
import com.example.ph19127_mob2041.dao.SachDAO;
import com.example.ph19127_mob2041.dao.ThanhVienDAO;
import com.example.ph19127_mob2041.dao.ThuThuDAO;
import com.example.ph19127_mob2041.fragment.ThuThuFragment;
import com.example.ph19127_mob2041.fragment.ChangePassFragment;
import com.example.ph19127_mob2041.fragment.DoanhThuFragment;
import com.example.ph19127_mob2041.fragment.LoaiSachFragment;
import com.example.ph19127_mob2041.fragment.PhieuMuonFragment;
import com.example.ph19127_mob2041.fragment.SachFragment;
import com.example.ph19127_mob2041.fragment.ThanhVienFragment;
import com.example.ph19127_mob2041.fragment.TopFragment;
import com.example.ph19127_mob2041.model.LoaiSach;
import com.example.ph19127_mob2041.model.PhieuMuon;
import com.example.ph19127_mob2041.model.Sach;
import com.example.ph19127_mob2041.model.ThanhVien;
import com.example.ph19127_mob2041.model.ThuThu;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    //TODO dimen string resource
    private static final int FRAGMENT_PHIEU_MUON = 0;
    private static final int FRAGMENT_LOAI_SACH = 1;
    private static final int FRAGMENT_SACH = 2;
    private static final int FRAGMENT_THANH_VIEN = 3;
    private static final int FRAGMENT_ADD_USER = 4;
    private static final int FRAGMENT_TOP = 5;
    private static final int FRAGMENT_DOANH_THU = 6;
    private static final int FRAGMENT_CHANGE_PASS = 7;
    private int mCurrentFragment = FRAGMENT_PHIEU_MUON;
    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private int idNavChecked = R.id.nav_MainActivity_PhieuMuon;

    private List<PhieuMuon> phieuMuonList;
    private List<ThanhVien> thanhVienList;
    private List<ThuThu> thuThuList;
    private List<LoaiSach> loaiSachList;
    private List<Sach> sachList;

    private PhieuMuonDAO phieuMuonDAO;
    private SachDAO sachDAO;
    private LoaiSachDAO loaiSachDAO;
    private ThuThuDAO thuThuDAO;
    private ThanhVienDAO thanhVienDAO;

    private PhieuMuonFragment phieuMuonFragment;
    private SachFragment sachFragment;
    private LoaiSachFragment loaiSachFragment;
    private ThanhVienFragment thanhVienFragment;
    private ThuThuFragment thuThuFragment;

    private TopFragment topFragment;
    private DoanhThuFragment doanhThuFragment;
    private ChangePassFragment changePassFragment;
    //TODO memory leak in 7 fragments


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        boolean isAdmin = intent.getBooleanExtra("isAdmin", false);
        String userName = "Xin chào " + intent.getStringExtra("name");
        String userId = intent.getStringExtra("id");
        //lấy tên người đăng nhập
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findView();
        //findviewByID
        View headerView = navigationView.getHeaderView(0);
        TextView navUserName = headerView.findViewById(R.id.tv_MainActivity_label);
        navUserName.setText(userName);
        if (isAdmin) navigationView.getMenu()
                .findItem(R.id.nav_MainActivity_thuThu)
                .setVisible(true)
                .setEnabled(true);
        else navigationView.getMenu()
                .findItem(R.id.nav_MainActivity_thuThu)
                .setVisible(false)
                .setEnabled(false);

//        DBHelper dbHelper = new DBHelper(this);
        //TODO test database
        createDao();
        createRepository();
        getData();

        thanhVienFragment = new ThanhVienFragment(thanhVienList, thanhVienDAO, this);
        sachFragment = new SachFragment(sachList, loaiSachList, sachDAO);
        loaiSachFragment = new LoaiSachFragment(loaiSachList, loaiSachDAO);
        phieuMuonFragment = new PhieuMuonFragment(phieuMuonList,
                thanhVienList,
                thuThuList,
                loaiSachList,
                sachList,
                phieuMuonDAO,
                userId);
        thuThuFragment = new ThuThuFragment(thuThuList, thuThuDAO);
        topFragment = TopFragment.newInstance(userId,userName);
        doanhThuFragment = new DoanhThuFragment();
        changePassFragment = ChangePassFragment.newInstance(userId, userName);

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                mDrawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        //tạo toggle đóng mở drawer
        mDrawerLayout.addDrawerListener(toggle);
        //thêm listener vào drawrlayout
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.getMenu().findItem(R.id.nav_MainActivity_PhieuMuon).setChecked(true);
        getSupportActionBar().setTitle("Quản lý phiếu mượn");
        replaceFragment(phieuMuonFragment, "Quản lý phiếu mượn");



    }

    private void createRepository() {
        phieuMuonList = new ArrayList<>();
        sachList = new ArrayList<>();
        loaiSachList = new ArrayList<>();
        thanhVienList = new ArrayList<>();
        thuThuList =  new ArrayList<>();
    }

    private void createDao() {
        phieuMuonDAO = new PhieuMuonDAO(this);
        loaiSachDAO = new LoaiSachDAO(this);
        sachDAO = new SachDAO(this);
        thanhVienDAO = new ThanhVienDAO(this);
        thuThuDAO = new ThuThuDAO(this);
    }

    private void getData() {
        phieuMuonList.addAll(phieuMuonDAO.getAll());
        sachList.addAll(sachDAO.getAll());
        loaiSachList.addAll(loaiSachDAO.getAll() );
        thanhVienList.addAll(thanhVienDAO.getAll());
        thuThuList.addAll(thuThuDAO.getAll());
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_MainActivity_PhieuMuon:
                if (mCurrentFragment != FRAGMENT_PHIEU_MUON) {
                    replaceFragment(phieuMuonFragment, "Quản lý phiếu mượn");
                    mCurrentFragment = FRAGMENT_PHIEU_MUON;
                }
                break;

            case R.id.nav_MainActivity_LoaiSach:
                if (mCurrentFragment != FRAGMENT_LOAI_SACH) {
                    replaceFragment(loaiSachFragment, "Quản lý loại sách");
                    mCurrentFragment = FRAGMENT_LOAI_SACH;
                }
                break;

            case R.id.nav_MainActivity_Sach:
                if (mCurrentFragment != FRAGMENT_SACH) {
                    replaceFragment(sachFragment, "Quản lý sách");
                    mCurrentFragment = FRAGMENT_SACH;
                }
                break;

            case R.id.nav_MainActivity_thanhVien:
                if (mCurrentFragment != FRAGMENT_THANH_VIEN) {
                    replaceFragment(thanhVienFragment, "Quản lý thành viên");
                    mCurrentFragment = FRAGMENT_THANH_VIEN;
                }
                break;

            case R.id.nav_MainActivity_thuThu:
                if (mCurrentFragment != FRAGMENT_ADD_USER) {
                    replaceFragment(thuThuFragment, "Quản lý thủ thư");
                    mCurrentFragment = FRAGMENT_ADD_USER;
                }
                break;

            case R.id.nav_MainActivity_top10Sach:
                if (mCurrentFragment != FRAGMENT_TOP) {
                    replaceFragment(topFragment, "Top 10 sách mượn nhiều nhất");
                    mCurrentFragment = FRAGMENT_TOP;
                }
                break;

            case R.id.nav_MainActivity_doanhThu:
                if (mCurrentFragment != FRAGMENT_DOANH_THU) {
                    replaceFragment(doanhThuFragment, "Doanh thu");
                    mCurrentFragment = FRAGMENT_DOANH_THU;
                }
                break;

            case R.id.nav_MainActivity_doiMatKhau:
                if (mCurrentFragment != FRAGMENT_CHANGE_PASS) {
                    replaceFragment(changePassFragment, "Đổi mật khẩu");
                    mCurrentFragment = FRAGMENT_CHANGE_PASS;
                }
                break;
            case R.id.nav_MainActivity_dangXuat:
                finish();
                break;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        navigationView.getMenu().findItem(idNavChecked).setChecked(false);
        //unchecked current item
        idNavChecked = id;
        //idNavChecked = current itemSelected
        navigationView.getMenu().findItem(id).setChecked(true);
        //checked current item
        return true;
    }
    private void replaceFragment(Fragment fragment, String title) {
        toolbar.setTitle(title);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout_mainActivity_content, fragment);
        transaction.commit();

    }

    private void findView() {
        toolbar = findViewById(R.id.toolbar_mainActivity_titleFragment);
        mDrawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigationView_mainActivity_navigateFragment);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START))
            mDrawerLayout.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }
}