package com.example.quizz_project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.QuestionViewHolder> {

    private List<QuestionsListActivity.Question> questions;
    private OnQuestionClickListener listener;

    public interface OnQuestionClickListener {
        void onQuestionClick(int position);
    }

    public QuestionsAdapter(List<QuestionsListActivity.Question> questions, OnQuestionClickListener listener) {
        this.questions = questions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_question, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        QuestionsListActivity.Question question = questions.get(position);

        holder.questionText.setText(question.getText());
        holder.categoryText.setText(question.getCategory());

        // Check if the question has been answered
        if (question.getIsCorrect() == 0) {  // Not answered yet
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.gray));
            holder.itemView.setClickable(true);  // Enable click for unanswered questions
        } else if (question.getIsCorrect() == 2) {  // Correct answer
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.green));
            holder.itemView.setClickable(false);  // Disable click for answered questions
        } else if (question.getIsCorrect() == 1) {  // Incorrect answer
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.red));
            holder.itemView.setClickable(false);  // Disable click for answered questions
        }
    }



    @Override
    public int getItemCount() {
        return questions.size();
    }

    class QuestionViewHolder extends RecyclerView.ViewHolder {
        TextView questionText;
        TextView categoryText;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            questionText = itemView.findViewById(R.id.questionTitle);
            categoryText = itemView.findViewById(R.id.questionCategory);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onQuestionClick(position);
                }
            });
        }
    }
}