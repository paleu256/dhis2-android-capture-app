package org.dhis2.usescases.datasets.dataSetTable;

import org.dhis2.data.forms.dataentry.tablefields.RowAction;
import org.dhis2.data.tuples.Pair;
import org.dhis2.usescases.datasets.datasetInitial.DataSetInitialModel;
import org.dhis2.usescases.general.AbstractActivityContracts;
import org.hisp.dhis.android.core.category.CategoryModel;
import org.hisp.dhis.android.core.category.CategoryOptionModel;
import org.hisp.dhis.android.core.dataelement.DataElementModel;
import org.hisp.dhis.android.core.dataset.DataSetModel;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModel;
import org.hisp.dhis.android.core.period.PeriodType;

import java.util.Date;
import java.util.List;
import java.util.Map;


import io.reactivex.Flowable;

public class DataSetTableContract {

    public interface View extends AbstractActivityContracts.View {

        void setDataElements(Map<String, List<DataElementModel>> data, Map<String, List<List<Pair<CategoryOptionModel, CategoryModel>>>> stringListMap);

        void setDataValue(List<DataSetTableModel> data);

        Boolean accessDataWrite();

        Flowable<RowAction> rowActions();

        void showOptions(boolean open);

        void showOrgUnitDialog(List<OrganisationUnitModel> data);

        void showPeriodSelector(PeriodType periodType);

        OrganisationUnitModel getSelectedOrgUnit();

        Date getSelectedPeriod();

        String getDataSetUid();

        String getSelectedCatOptions();

        String getOrgUnitName();

        void showCatComboSelector(String catOptionUid, List<CategoryOptionModel> data);

        void setData(DataSetInitialModel dataSetInitialModel);



    }

    public interface Presenter extends AbstractActivityContracts.Presenter {

        void onBackClick();

        void init(View view, String orgUnitUid, String periodTypeName, String periodInitialDate, String catCombo);

        String getOrgUnitUid();
        String getPeriodTypeName();
        String getPeriodInitialDate();
        String getCatCombo();

        void optionsClick();

        void onOrgUnitSelectorClick();

        void onReportPeriodClick();

        void onRefreshClick();

        void onCatOptionClick(String catOptionUid);
    }

}