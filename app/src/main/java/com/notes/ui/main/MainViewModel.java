package com.notes.ui.main;

import android.graphics.pdf.PdfDocument;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.notes.data.Notes;
import com.notes.data.NotesRepository;
import com.notes.utils.PDFDocumentUtils;
import java.util.List;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class MainViewModel extends ViewModel {

    private final NotesRepository notesRepository;
    private PdfDocument pdfDocument;
    private final MutableLiveData<List<Notes>> notesLiveData = new MutableLiveData<>();
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Inject
    public MainViewModel(NotesRepository notesRepository){
        this.notesRepository = notesRepository;
        getNotes();
    }

    private void getNotes() {
        compositeDisposable.add(notesRepository.getNotes().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                notes -> getNotesLiveData().setValue(notes)
        ));
    }

    public MutableLiveData<List<Notes>> getNotesLiveData() {
        return notesLiveData;
    }

    public Completable deleteNote(int id) {
        return notesRepository.deleteNote(id).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
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
