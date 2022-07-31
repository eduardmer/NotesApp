package com.notes.ui.main;

import android.graphics.pdf.PdfDocument;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.notes.R;
import com.notes.data.Notes;
import com.notes.data.NotesRepository;
import com.notes.utils.PDFDocumentUtils;
import com.notes.utils.Resources;
import com.notes.utils.Status;
import java.util.List;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class MainViewModel extends ViewModel {

    private final NotesRepository notesRepository;
    private PdfDocument pdfDocument;
    private final MutableLiveData<Resources<List<Notes>>> state;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Inject
    public MainViewModel(NotesRepository notesRepository){
        this.notesRepository = notesRepository;
        this.state = new MutableLiveData<>(new Resources.Loading<>());
        getNotes();
    }

    public MutableLiveData<Resources<List<Notes>>> getState() {
        return state;
    }

    private void getNotes() {
        compositeDisposable.add(notesRepository.getNotes().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                notes -> getState().setValue(new Resources.Success<>(Status.SUCCESS, notes, -1))
        ));
    }

    public void deleteNote(int id) {
        compositeDisposable.add(notesRepository.deleteNote(id).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                () -> state.postValue(new Resources.Success<>(Status.SUCCESS, null, R.string.delete_note)),
                error -> state.postValue(new Resources.Error<>(Status.ERROR, null, R.string.error))
        ));
    }

    public Single<PdfDocument> createPdfDocument(String title, String description) {
        return Single.fromCallable(() -> {
            pdfDocument = PDFDocumentUtils.createPDFDocument(title, description);
            return pdfDocument;
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public PdfDocument getPdfDocument() {
        return pdfDocument;
    }

    @Override
    protected void onCleared() {
        compositeDisposable.clear();
        super.onCleared();
    }
}
