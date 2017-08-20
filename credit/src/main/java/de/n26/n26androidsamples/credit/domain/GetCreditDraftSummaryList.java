package de.n26.n26androidsamples.credit.domain;

import android.support.annotation.NonNull;

import java.util.List;

import javax.inject.Inject;

import de.n26.n26androidsamples.base.common.rx.UnwrapOptionTransformer;
import de.n26.n26androidsamples.base.domain.ReactiveInteractor.GetInteractor;
import de.n26.n26androidsamples.credit.data.CreditDraftSummary;
import de.n26.n26androidsamples.credit.data.CreditRepository;
import io.reactivex.Flowable;
import polanski.option.Option;

public class GetCreditDraftSummaryList implements GetInteractor<Void, List<CreditDraftSummary>> {

    @NonNull
    private final CreditRepository creditRepository;

    @Inject
    GetCreditDraftSummaryList(@NonNull final CreditRepository creditRepository) {
        this.creditRepository = creditRepository;
    }

    /**
     Emits updates of the credit draft summaries.
     It will fetch in the case the repository has no local data stored.

     @param params no params required, pass {@link Option#NONE}
     */
    @NonNull
    @Override
    public Flowable<List<CreditDraftSummary>> getBehaviorStream(@NonNull final Option<Void> params) {
        return creditRepository.getCreditDraftSummaryListBehaviorStream()
                               .compose(this::fetchIfRepositoryIsEmpty)
                               .compose(new UnwrapOptionTransformer<>());
    }

    @NonNull
    private Flowable<Option<List<CreditDraftSummary>>> fetchIfRepositoryIsEmpty(@NonNull final Flowable<Option<List<CreditDraftSummary>>> drafts) {
        return drafts.filter(Option::isNone)
                     .flatMapCompletable(it -> creditRepository.fetchCreditDraftSummariesSingle())
                     .andThen(drafts);
    }
}