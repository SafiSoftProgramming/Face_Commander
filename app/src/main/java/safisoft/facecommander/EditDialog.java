package safisoft.facecommander;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class EditDialog extends Dialog implements
        View.OnClickListener {

    public ImageButton btn_ok_in_dialog, btn_cancel_in_dialog;
    TextView txtv_command_name ;
    ImageView imgv_command_pic ;
    TextView txtv_command_first_head ;
    EditText edtxt_command_first ;
    TextView txtv_command_first_des ;
    TextView txtv_command_second_head ;
    EditText edtxt_command_second ;
    TextView txtv_command_second_des ;
    public Activity c;

    public EditDialog(@NonNull Activity a) {
        super(a);
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.edit_dialog);
        btn_ok_in_dialog = findViewById(R.id.btn_ok_in_dialog);
        btn_cancel_in_dialog = findViewById(R.id.btn_cancel_in_dialog);
        txtv_command_name = findViewById(R.id.txtv_command_name);
        imgv_command_pic =findViewById(R.id.imgv_command_pic);
        txtv_command_first_head = findViewById(R.id.txtv_command_first_head);
        edtxt_command_first = findViewById(R.id.edtxt_command_first);
        txtv_command_first_des = findViewById(R.id.txtv_command_first_des);
        txtv_command_second_head = findViewById(R.id.txtv_command_second_head);
        edtxt_command_second = findViewById(R.id.edtxt_command_second);
        txtv_command_second_des = findViewById(R.id.txtv_command_second_des);






        btn_ok_in_dialog.setOnClickListener(this);
        btn_cancel_in_dialog.setOnClickListener(this);



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok_in_dialog:
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
