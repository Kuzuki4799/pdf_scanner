package com.shockwave.pdf_scaner.ui;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.shockwave.pdf_scaner.R;
import com.shockwave.pdf_scaner.adapter.AlbumAdapter;
import com.shockwave.pdf_scaner.adapter.MiniSelectAdapter;
import com.shockwave.pdf_scaner.adapter.SelectAdapter;
import com.shockwave.pdf_scaner.base.BaseActivity;
import com.shockwave.pdf_scaner.callback.ItemMoveCallbackListener;
import com.shockwave.pdf_scaner.callback.OnStartDragListener;
import com.shockwave.pdf_scaner.model.PhotoDirectory;
import com.shockwave.pdf_scaner.util.FileUtils;
import com.shockwave.pdf_scaner.util.ParamUtils;
import com.shockwave.pdf_scaner.util.RecyclerUtils;

import java.util.ArrayList;

public class ImageActivity extends BaseActivity implements View.OnClickListener, OnStartDragListener, MiniSelectAdapter.OnReset {

    private AppCompatImageView imgBack;
    private AppCompatTextView txtTitle;
    private AppCompatTextView txtNext;
    private RecyclerView recyclerView;
    private RecyclerView recyclerViewFolder;
    private AppCompatTextView textNoData;
    private RecyclerView recyclerViewImageSelected;
    private AppCompatTextView textNoDataSelect;

    private AlbumAdapter albumAdapter;
    private SelectAdapter selectAdapter;
    private MiniSelectAdapter miniSelectAdapter;

    private ItemTouchHelper touchHelper;

    private boolean isShowFolder = false;
    private ArrayList<PhotoDirectory.Media> listSelected = new ArrayList<>();
    private ArrayList<String> listImage = new ArrayList<>();

    @Override
    protected void initData() {
        setUpAdapter();
    }

    @Override
    protected void initView() {
        imgBack = findViewById(R.id.imgBack);
        txtTitle = findViewById(R.id.txtTitle);
        txtNext = findViewById(R.id.txtNext);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerViewFolder = findViewById(R.id.recyclerViewFolder);
        textNoData = findViewById(R.id.textNoData);
        recyclerViewImageSelected = findViewById(R.id.recyclerViewImageSelected);
        textNoDataSelect = findViewById(R.id.textNoDataSelect);
    }

    @Override
    protected void listener() {
        imgBack.setOnClickListener(this);
        txtTitle.setOnClickListener(this);
        txtNext.setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_image;
    }

    private void setUpAdapter() {
        albumAdapter = new AlbumAdapter(this, FileUtils.queryImages(this));
        RecyclerUtils.layoutLinear(this, recyclerViewFolder);
        recyclerViewFolder.setAdapter(albumAdapter);
        albumAdapter.setOnItemClickListener(position -> {
            ArrayList<PhotoDirectory.Media> listLocal = albumAdapter.list.get(position).medias;
            if (listSelected.size() != 0) {
                for (int i = 0; i < listSelected.size(); i++) {
                    for (int j = 0; j < listLocal.size(); j++) {
                        if (listSelected.get(i).id == listLocal.get(j).id) {
                            listLocal.get(j).isSelected = true;
                            listLocal.get(j).pos = i + 1;
                        }
                    }
                }
            }

            selectAdapter.setAll(listLocal);
            txtTitle.setText(albumAdapter.list.get(position).name.toUpperCase());
            handlerChange();

            if (listLocal.size() == 0) {
                textNoData.setVisibility(View.VISIBLE);
            } else {
                textNoData.setVisibility(View.GONE);
            }
        });

        RecyclerUtils.layoutGird(this, 3, 5, recyclerView);
        handlerSelectedAdapter();

        miniSelectAdapter = new MiniSelectAdapter(this, listSelected, this, this);
        RecyclerUtils.layoutLinearHorizontal(this, recyclerViewImageSelected);

        touchHelper = new ItemTouchHelper(new ItemMoveCallbackListener(miniSelectAdapter));
        touchHelper.attachToRecyclerView(recyclerViewImageSelected);

        recyclerViewImageSelected.setAdapter(miniSelectAdapter);
        miniSelectAdapter.setOnItemClickListener(position -> {
            listSelected.remove(position);
            handlerResetList();
            miniSelectAdapter.notifyDataSetChanged();
            if (listSelected.size() == 0) textNoDataSelect.setVisibility(View.VISIBLE);
        });
    }

    @SuppressLint("SetTextI18n")
    private void handlerResetList() {
        for (int i = 0; i < selectAdapter.list.size(); i++) {
            if (selectAdapter.list.get(i).isSelected) {
                selectAdapter.list.get(i).isSelected = false;
                selectAdapter.list.get(i).pos = i - 1;
                selectAdapter.notifyItemChanged(i);
            }
        }

        for (int i = 0; i < listSelected.size(); i++) {
            for (int j = 0; j < selectAdapter.list.size(); j++) {
                if (listSelected.get(i).id == selectAdapter.list.get(j).id) {
                    selectAdapter.list.get(j).isSelected = true;
                    selectAdapter.list.get(j).pos = i + 1;
                    selectAdapter.notifyItemChanged(j);
                }
            }
        }

        txtNext.setText("Next (" + listSelected.size() + ")");
    }

    @SuppressLint("SetTextI18n")
    private void handlerSelectedAdapter() {
        txtTitle.setText(albumAdapter.list.get(0).name.toUpperCase());
        selectAdapter = new SelectAdapter(this, albumAdapter.list.get(0).medias);

        recyclerView.setAdapter(selectAdapter);
        selectAdapter.setOnItemClickListener(position -> {
            selectAdapter.list.get(position).isSelected = !selectAdapter.list.get(position).isSelected;
            if (selectAdapter.list.get(position).isSelected) {
                listSelected.add(selectAdapter.list.get(position));
                selectAdapter.list.get(position).pos = listSelected.size();
                miniSelectAdapter.notifyDataSetChanged();
                textNoDataSelect.setVisibility(View.GONE);
                txtNext.setText("Next (" + listSelected.size() + ")");
            } else {
                if (listSelected.size() == 1) {
                    listSelected.remove(0);
                    handlerResetList();
                    miniSelectAdapter.notifyDataSetChanged();
                    textNoDataSelect.setVisibility(View.VISIBLE);
                } else {
                    for (int i = 0; i < listSelected.size(); i++) {
                        if (listSelected.get(i).id == selectAdapter.list.get(position).id) {
                            listSelected.remove(i);
                            handlerResetList();
                            miniSelectAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }
            }
            selectAdapter.notifyItemChanged(position);
            recyclerViewImageSelected.scrollToPosition(miniSelectAdapter.list.size() - 1);
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBack:
                finish();
                break;
            case R.id.txtTitle:
                handlerChange();
                break;
            case R.id.txtNext:
//                Bundle bundle = new Bundle();
//                bundle.putParcelableArrayList(ParamUtils.LINK, listSelected);
//                openNewActivity(bundle, EditVideoActivity.class);

                for (int i = 0; i < listSelected.size(); i++) {
                    listImage.add(listSelected.get(i).getPath(this));
                }
                Intent intent = new Intent(this, CropImageActivity.class);
                intent.putStringArrayListExtra(ParamUtils.LINK, listImage);
                intent.putExtra(ParamUtils.IS_ORC, false);
                startActivity(intent);
                break;

        }
    }

    private void handlerChange() {
        isShowFolder = !isShowFolder;
        recyclerViewFolder.setVisibility(isShowFolder ? View.VISIBLE : View.GONE);
        txtTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, isShowFolder ? R.drawable.ic_up : R.drawable.ic_down, 0);
    }

    @Override
    public void onStartDrag(MiniSelectAdapter.ViewHolder holder) {
        touchHelper.startDrag(holder);
    }

    @Override
    public void onResetList() {
        handlerResetList();
    }
}