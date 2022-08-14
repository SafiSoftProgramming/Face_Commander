package safisoft.facecommander;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Recent_Projects_Adapter extends RecyclerView.Adapter<Recent_Projects_Adapter.Recent_Projects_ViewHolder> {

    private NewProjectChooseActivity newProjectChooseActivity ;
    private List<safisoft.facecommander.Recent_Projects> recent_projects_list ;

    public Recent_Projects_Adapter(NewProjectChooseActivity newProjectChooseActivity, List<safisoft.facecommander.Recent_Projects> recent_projects_list) {

        this.newProjectChooseActivity = newProjectChooseActivity ;
        this.recent_projects_list = recent_projects_list ;


    }

    @NonNull
    @Override
    public Recent_Projects_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(newProjectChooseActivity);
        View view = inflater.inflate(R.layout.recent_project_list, null);
        return new Recent_Projects_ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Recent_Projects_ViewHolder holder, int position) {
        safisoft.facecommander.Recent_Projects recent_projects = recent_projects_list.get(position);
        holder.txtv_Recent_Project_name.setText(recent_projects.Get_Recent_Project_name());
      //  holder.txtv_Recent_Project_Type.setText(recent_projects.Get_Recent_Project_Type());
        holder.txtv_Recent_Project_Description.setText(recent_projects.Get_Recent_Project_Description());


    //    String Project_type = recent_projects.Get_Recent_Project_Type() ;

     //   if (Project_type.equals("Button Control")) {
        //    holder.imgv_Recent_Project_type.setBackgroundResource(R.drawable.ic_key_control);
    //    }
    //    if (Project_type.equals("Voice Control")){
       //     holder.imgv_Recent_Project_type.setBackgroundResource(R.drawable.ic_voice_control);
 //       }
    //    if (Project_type.equals("Rotation Control")){
      //      holder.imgv_Recent_Project_type.setBackgroundResource(R.drawable.ic_balanc_control);
   //     }


    }

    @Override
    public int getItemCount() {
        return recent_projects_list.size();
    }















    class Recent_Projects_ViewHolder extends RecyclerView.ViewHolder{

        TextView txtv_Recent_Project_name ;
   //     TextView txtv_Recent_Project_Type ;
        TextView txtv_Recent_Project_Description ;
        ImageButton btn_edit_Recent_Project ;
        ImageButton btn_delete_Recent_Project ;
        ImageButton btn_recent_project_list ;
     //   ImageView imgv_Recent_Project_type ;
        DbConnction db ;
        DeleteDialog deleteDialog ;
        Cursor c = null;
        View Del ;





        public Recent_Projects_ViewHolder(@NonNull View itemView) {
            super(itemView);

            db = new DbConnction(newProjectChooseActivity);
            try {
                db.createDataBase();
            } catch (IOException ioe) {
                throw new Error("Unable to create database");
            }
            try {
                db.openDataBase();
            } catch (SQLException sqle) {
                throw sqle;
            }


            txtv_Recent_Project_name = itemView.findViewById(R.id.txtv_Recent_Project_name);
          //  txtv_Recent_Project_Type = itemView.findViewById(R.id.txtv_Recent_Project_Type);
            txtv_Recent_Project_Description = itemView.findViewById(R.id.txtv_Recent_Project_Description);
            btn_edit_Recent_Project = itemView.findViewById(R.id.btn_edit_Recent_Project);
            btn_delete_Recent_Project = itemView.findViewById(R.id.btn_delete_Recent_Project);
            btn_recent_project_list = itemView.findViewById(R.id.btn_recent_project_list);
       //     imgv_Recent_Project_type = itemView.findViewById(R.id.imgv_Recent_Project_type);



            btn_recent_project_list.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION) {
                        safisoft.facecommander.Recent_Projects clickedDataItem = recent_projects_list.get(pos);


                        Intent intent = new Intent(newProjectChooseActivity, FindBluetoothActivity.class);
                        intent.putExtra("PROJECT_NAME",txtv_Recent_Project_name.getText().toString());
                        intent.putExtra("PROJECT_DESCRIPTION",txtv_Recent_Project_Description.getText().toString());
                        newProjectChooseActivity.startActivity(intent);

                    }
                }
            });



            btn_delete_Recent_Project.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION) {
                        safisoft.facecommander.Recent_Projects clickedDataItem = recent_projects_list.get(pos);

                        Del = v ;
                        deleteDialog = new DeleteDialog(newProjectChooseActivity);
                        deleteDialog.show();
                        deleteDialog.setCanceledOnTouchOutside(false);
                        deleteDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                        deleteDialog.btn_delete_in_dialog.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                db.deleteRecordAlternate("Projects_List", "_id", Integer.toString(clickedDataItem.Get_Id()));
                                db.deleteRecordAlternate("Project_Face_Control", "_id", Integer.toString(clickedDataItem.Get_Id()));


                                SnackBarInfoControl snackBarInfoControl = new SnackBarInfoControl();
                                snackBarInfoControl.SnackBarInfoControlView(newProjectChooseActivity.getApplicationContext(), Del,newProjectChooseActivity,"Project Deleted");

                                newProjectChooseActivity.Recent_Projects_List.clear();
                                Recent_Projects_Adapter recent_projects_adapter = new Recent_Projects_Adapter(newProjectChooseActivity,recent_projects_list);
                                newProjectChooseActivity.recyclerView.setAdapter(recent_projects_adapter);
                                recent_projects_adapter.notifyDataSetChanged();
                                recent_projects_list = new ArrayList<>();
                                c = db.Query_Data("Projects_List", null, null, null, null, null, null);
                                c =  db.sortRecord_id();
                                c.moveToFirst();
                                if (c.moveToFirst()) {
                                    do {
                                        recent_projects_list.add(new safisoft.facecommander.Recent_Projects(c.getInt(0),c.getString(1),c.getString(2)));
                                    }while (c.moveToNext());
                                }
                                Recent_Projects_Adapter recentProjectsAdapter = new Recent_Projects_Adapter(newProjectChooseActivity,recent_projects_list);
                                newProjectChooseActivity.recyclerView.setAdapter(recentProjectsAdapter);
                                if(recent_projects_list.isEmpty()){
                                    //  img_no_data.setVisibility(View.VISIBLE);
                                }
                                else {
                                    //  img_no_data.setVisibility(View.GONE);
                                }
                                deleteDialog.dismiss();
                            }
                        });

                        deleteDialog.btn_cancel_in_dialog.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                deleteDialog.dismiss();
                            }
                        });
                    }
                    }
                });


            btn_edit_Recent_Project.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent_Handel(EditFaceCommandControlActivity.class);

                }
            });




        }


        private void Intent_Handel(Class Activity){
            Intent intent = new Intent(newProjectChooseActivity,Activity);
            intent.putExtra("PROJECT_NAME",txtv_Recent_Project_name.getText().toString());
            intent.putExtra("PROJECT_CONTROL_DESCRIPTION", txtv_Recent_Project_Description.getText().toString());
            intent.putExtra("PROJECT_STATE","EDIT");
            intent.putExtra("AD_STATE","false" );
            newProjectChooseActivity.startActivity(intent);

        }







    }

}
