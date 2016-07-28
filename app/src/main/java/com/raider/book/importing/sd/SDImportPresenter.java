package com.raider.book.importing.sd;

//public class SDImportPresenter implements BasePresenter {
//
//    private SDImportContract.View iView;
//    private SDImportContract.Model iModel;
//    private Subscription subscription1;
//    private Subscription subscription2;
//
//    public SDImportPresenter(SDImportContract.View iView, SDImportContract.Model iModel) {
//        this.iView = iView;
//        this.iModel = iModel;
//        iView._setPresenter(this);
//    }
//
//    /**
//     * get all .txt files in SD and show them in a RecyclerView
//     */
//    public void start() {
//        iView._showProgress();
//        subscription1 = _getObservable1().subscribe(_getSubscriber1());
//    }
//
//    private rx.Observable<ArrayList<BookData>> _getObservable1() {
//        return Observable.just(true)
//                .map(new Func1<Boolean, ArrayList<BookData>>() {
//                    @Override
//                    public ArrayList<BookData> call(Boolean aBoolean) {
//                        return iModel.traverse();
//                    }
//                })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread());
//    }
//
//    private Action1<ArrayList<BookData>> _getSubscriber1() {
//        return new Action1<ArrayList<BookData>>() {
//            @Override
//            public void call(ArrayList<BookData> books) {
//                iView._showBooks(books);
//                iView._hideProgress();
//            }
//        };
//    }
//
//    /**
//     * add certain books to db
//     *
//     * @param sparseIntArray chosen books
//     */
//    public void addToShelf(SparseIntArray sparseIntArray) {
//        iView._showProgress();
//        subscription2 = _getObservable2(sparseIntArray).subscribe(_getSubscriber2());
//    }
//
//    private rx.Observable<ArrayList<BookData>> _getObservable2(SparseIntArray sparseIntArray) {
//        return Observable.just(sparseIntArray)
//                .map(new Func1<SparseIntArray, ArrayList<BookData>>() {
//                    @Override
//                    public ArrayList<BookData> call(SparseIntArray sparseIntArray) {
//                        return iModel.save2DB(sparseIntArray);
//                    }
//                })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread());
//    }
//
//    private Action1<ArrayList<BookData>> _getSubscriber2() {
//        return new Action1<ArrayList<BookData>>() {
//            @Override
//            public void call(ArrayList<BookData> addedBooks) {
//                iView._handleAddBookSuccess(addedBooks);
//                iView._hideProgress();
//            }
//        };
//    }
//
//    @Override
//    public void onViewCreated() {
//
//    }
//
//    @Override
//    public void onDestroy() {
//        iModel = null;
//        if (subscription1 != null) {
//            subscription1.unsubscribe();
//        }
//        if (subscription2 != null) {
//            subscription2.unsubscribe();
//        }
//    }

//}
