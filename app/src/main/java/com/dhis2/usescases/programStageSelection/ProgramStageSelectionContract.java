package com.dhis2.usescases.programStageSelection;

import android.support.annotation.NonNull;

import com.dhis2.usescases.general.AbstractActivityContracts;

import org.hisp.dhis.android.core.program.ProgramStageModel;

import java.util.List;

public class ProgramStageSelectionContract {

    public interface View extends AbstractActivityContracts.View {

        void setData(List<ProgramStageModel> programStageModels);
    }

    public interface Presenter extends AbstractActivityContracts.Presenter {

        void onBackClick();

        void getProgramStages(@NonNull String programUid, @NonNull View view);

        void onProgramStageClick(ProgramStageModel programStage);

        void setEventCreationType(String eventCreationType);

        void setOrgUnit(String orgUnit);
    }
}
