package com.example.animedb.presenter;

import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import com.example.animedb.R;
import com.example.animedb.model.Subject;
import com.example.animedb.view.SubjectView;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;

/**
 * Created by Fang on 2017/5/17.
 *
 */

public class SubjectPresenter implements Presenter<SubjectView>, View.OnClickListener {
    private SubjectView subjectView;
    private Subject subject;


    public SubjectPresenter(SubjectView view, Subject subject) {
        attachView(view);
        this.subject = subject;
    }

    @Override
    public void attachView(SubjectView view) {
        this.subjectView = view;
    }

    @Override
    public void detachView() {
        if (subjectView != null) {
            subjectView = null;
        }
        if (subject != null) {
            subject = null;
        }
    }

    @Override
    public void onClick(View v) {
        TextView textView;
        AlertDialog.Builder builder;
        switch (v.getId()) {
            case R.id.layout_subject_summary:
                String summary = subjectView.getSubject().getSummary();
                textView = new TextView(subjectView.getContext());
                textView.setText(summary);
                textView.setTextIsSelectable(true);
                textView.setPadding(40, 40, 40, 40);
                builder = new AlertDialog.Builder(subjectView.getContext());
                builder.setTitle(R.string.summary)
                        .setView(textView)
                        .show();
                break;
            case R.id.layout_subject_alias:
                String alias = subjectView.getSubject().getAlias();
                textView = new TextView(subjectView.getContext());
                textView.setText(alias.substring(0, alias.length() - 1));
                textView.setTextIsSelectable(true);
                textView.setPadding(40, 40, 40, 40);
                builder = new AlertDialog.Builder(subjectView.getContext());
                builder.setTitle(R.string.alias)
                        .setView(textView)
                        .show();
                break;
            case R.id.layout_subject_score:
                int numColumns = 10;
                int[] scores = subjectView.getSubject().getScores();
                List<Column> columns = new ArrayList<>();
                List<SubcolumnValue> values;
                List<AxisValue> x_list = new ArrayList<>();

                for (int i = 0; i < numColumns; ++i) {
                    values = new ArrayList<>();
                    values.add(new SubcolumnValue(scores[i + 1], ChartUtils.nextColor()));
                    Column column = new Column(values);
                    column.setHasLabels(true);
                    columns.add(column);

                    x_list.add(new AxisValue(i).setLabel(String.valueOf(i + 1)));
                }
                ColumnChartData chartData = new ColumnChartData(columns);
                Axis axisX = new Axis();
                axisX.setValues(x_list);
                chartData.setAxisXBottom(axisX);


                View dialogView = View.inflate(subjectView.getContext(), R.layout.dialog_subject_scores, null);

                ColumnChartView chartView = (ColumnChartView) dialogView.findViewById(R.id.chart_subject_scores);
                chartView.setColumnChartData(chartData);


                builder = new AlertDialog.Builder(subjectView.getContext());
                builder.setView(dialogView)
                        .setTitle(R.string.scores)
                        .show();


                break;
            case R.id.fab_subject:
                if (subjectView.getFavorited()) {
                    subject.removeFromFavorites(subjectView.getDBHelper());
                    subjectView.setFavorited(false);
                }
                else {
                    subject.addToFavorite(subjectView.getDBHelper());
                    subjectView.setFavorited(true);
                }
                break;
            default:
        }

    }
}
