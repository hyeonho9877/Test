package com.kyonggi.eku.view.board.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.kyonggi.eku.R;
import com.kyonggi.eku.databinding.ActivityBoardBinding;
import com.kyonggi.eku.model.FreeBoardPreview;
import com.kyonggi.eku.model.InfoBoardPreview;
import com.kyonggi.eku.presenter.board.BoardPresenter;
import com.kyonggi.eku.utils.callbacks.OnBoardResponseListeners;
import com.kyonggi.eku.view.board.fragment.FragmentFreeBoard;
import com.kyonggi.eku.view.board.fragment.FragmentInfoBoard;

import java.util.List;

public class ActivityBoard extends AppCompatActivity implements OnBoardResponseListeners {

    private static final String TAG = "ActivityBoard";
    private ActivityBoardBinding binding;
    private BoardPresenter presenter;
    private final FragmentManager fragmentManager = getSupportFragmentManager();
    private String currentMode;
    private final FragmentFreeBoard fragmentFreeBoard = new FragmentFreeBoard();
    private final FragmentInfoBoard fragmentInfoBoard = new FragmentInfoBoard();
    private String buildingNumber;
    private String rawBuildingNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        binding = ActivityBoardBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        String buildingNumber = getIntent().getStringExtra("buildingNumber");
        Log.d(TAG, "onCreate: " + buildingNumber);
        setBoard();

        presenter = new BoardPresenter(this, this);
        initListeners();
    }

    private void initListeners() {
        binding.buttonFreeBoard.setOnClickListener(v -> {
            if (currentMode.equals(BOARD_INFO)) switchBoard();
        });
        binding.buttonInfoBoard.setOnClickListener(v -> {
            if (currentMode.equals(BOARD_FREE)) switchBoard();
        });
        binding.buttonWriteBoard.setOnClickListener(v -> {
            if (presenter.isAuthenticated()) {
                if (currentMode.equals(BOARD_INFO)) presenter.writeInfoBoard();
                else presenter.writeFreeBoard();
            } else presenter.signIn();
        });
        binding.buttonSearch.setOnClickListener(v -> {
            presenter.search(currentMode, buildingNumber);
        });

    }

    private void setBoard() {
        rawBuildingNumber = getIntent().getStringExtra("buildingNumber");
        if (rawBuildingNumber.contains("종합")) buildingNumber = "5";
        else if(rawBuildingNumber.contains("공학")) buildingNumber = "10";
        else if(rawBuildingNumber.equals("EKU")) buildingNumber = "0";
        else buildingNumber = rawBuildingNumber.substring(0, 1);
        binding.textBuildingName.setText(rawBuildingNumber);
        currentMode = getIntent().getStringExtra("mode");

        if (currentMode.equals(BOARD_FREE)) {
            binding.textBuildingBoardAnnounce.setText("자유 게시판입니다.");
            binding.buttonFreeBoard.setTextColor(Color.parseColor("#F7941E"));
            binding.buttonInfoBoard.setTextColor(Color.parseColor("#80252525"));
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_layout_board, fragmentFreeBoard, BOARD_FREE)
                    .commit();
        } else if (currentMode.equals(BOARD_INFO)) {
            String announce = rawBuildingNumber + " 공지 게시판 입니다.";
            binding.textBuildingBoardAnnounce.setText(announce);
            binding.buttonInfoBoard.setTextColor(Color.parseColor("#F7941E"));
            binding.buttonFreeBoard.setTextColor(Color.parseColor("#80252525"));

            fragmentManager.beginTransaction()
                    .replace(R.id.frame_layout_board, fragmentInfoBoard, BOARD_INFO)
                    .commit();
        }
    }

    private void switchBoard() {
        if (currentMode.equals(BOARD_FREE)) {
            String announce = rawBuildingNumber + " 공지게시판입니다.";
            binding.textBuildingBoardAnnounce.setText(announce);
            binding.buttonFreeBoard.setTextColor(Color.parseColor("#80252525"));
            binding.buttonInfoBoard.setTextColor(Color.parseColor("#F7941E"));
            currentMode = BOARD_INFO;

            fragmentManager.beginTransaction()
                    .replace(R.id.frame_layout_board, fragmentInfoBoard, BOARD_INFO)
                    .commit();

        } else if (currentMode.equals(BOARD_INFO)) {
            binding.textBuildingBoardAnnounce.setText("자유 게시판입니다.");
            binding.buttonInfoBoard.setTextColor(Color.parseColor("#80252525"));
            binding.buttonFreeBoard.setTextColor(Color.parseColor("#F7941E"));
            currentMode = BOARD_FREE;

            fragmentManager.beginTransaction()
                    .replace(R.id.frame_layout_board, fragmentFreeBoard, BOARD_FREE)
                    .commit();
        }
    }

    public void getInfoBoardArticles() {
        if (buildingNumber.equals("0")) presenter.loadAllInfoArticles();
        else presenter.getInfoBoardArticles(buildingNumber);
    }

    public void getFreeBoardArticles() {
        presenter.getFreeBoardArticles();
    }

    @Override
    public void onFreeBoardSuccess(List<FreeBoardPreview> articles, String purpose) {
        binding.animBoardLoading.setVisibility(View.INVISIBLE);
        binding.frameLayoutBoard.setVisibility(View.VISIBLE);
        if (purpose.equals(LOAD_RECENT))
            fragmentFreeBoard.updateArticles(articles);
        else if (purpose.equals(INIT))
            fragmentFreeBoard.listArticles(articles);
        else fragmentFreeBoard.loadMoreArticles(articles);
    }

    @Override
    public void onInfoBoardSuccess(List<InfoBoardPreview> articles, String purpose) {
        binding.animBoardLoading.setVisibility(View.INVISIBLE);
        binding.frameLayoutBoard.setVisibility(View.VISIBLE);
        if (purpose.equals(LOAD_RECENT))
            fragmentInfoBoard.updateArticles(articles);
        else if (purpose.equals(INIT))
            fragmentInfoBoard.listArticles(articles);
        else fragmentInfoBoard.loadMoreArticles(articles);
    }

    @Override
    public void onFailed() {
        Toast.makeText(this, "게시글 조회에 실패하였습니다.", Toast.LENGTH_SHORT).show();
    }

    public void updateInfoBoard(long id) {
        presenter.updateInfoBoard(id, buildingNumber);
    }

    public void loadMoreInfoArticles(long no) {
        presenter.loadMoreInfoArticles(no, buildingNumber);
    }

    public void loadMoreAllInfoArticles(long id) {
        presenter.loadMoreAllInfo(id);
    }

    public void loadMoreFreeArticles(Long id) {
        presenter.loadMoreFreeArticles(id);
    }

    public static final String BOARD_FREE = "BOARD_FREE";
    public static final String BOARD_INFO = "BOARD_INFO";
    public static final String LOAD_RECENT = "LOAD_RECENT";
    public static final String LOAD_OLD = "LOAD_OLD";
    public static final String INIT = "INIT";
}