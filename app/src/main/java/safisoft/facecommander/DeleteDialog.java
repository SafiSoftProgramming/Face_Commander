package safisoft.facecommander;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;

import androidx.annotation.NonNull;

public class DeleteDialog extends Dialog implements
        View.OnClickListener {

    public ImageButton btn_delete_in_dialog, btn_cancel_in_dialog;
    public Activity c;

    public DeleteDialog(@NonNull Activity a) {
        super(a);
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.delete_dialog);
        btn_delete_in_dialog = findViewById(R.id.btn_delete_in_dialog);
        btn_cancel_in_dialog = findViewById(R.id.btn_cancel_in_dialog);
        btn_delete_in_dialog.setOnClickListener(this);
        btn_cancel_in_dialog.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_delete_in_dialog:
                c.finish();
                break;
            case R.id.btn_cancel_in_dialog:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();

    }
}
