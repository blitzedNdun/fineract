/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.integrationtests.loan;

import com.google.gson.Gson;
import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.fineract.client.models.GetLoansLoanIdResponse;
import org.apache.fineract.client.models.GetLoansResponse;
import org.apache.fineract.client.models.PostLoanProductsRequest;
import org.apache.fineract.client.models.PostLoanProductsResponse;
import org.apache.fineract.client.models.PostLoansLoanIdResponse;
import org.apache.fineract.client.models.PostLoansRequest;
import org.apache.fineract.client.models.PostLoansResponse;
import org.apache.fineract.integrationtests.BaseLoanIntegrationTest;
import org.apache.fineract.integrationtests.common.ClientHelper;
import org.apache.fineract.integrationtests.common.CollateralManagementHelper;
import org.apache.fineract.integrationtests.common.Utils;
import org.junit.jupiter.api.Test;

public class LoanApiIntegrationTest extends BaseLoanIntegrationTest {

    @Test
    public void test_retrieveLoansByClientId_Works() {
        AtomicLong createdLoanId = new AtomicLong();
        AtomicLong createdLoanId2 = new AtomicLong();
        Long clientId = clientHelper.createClient(ClientHelper.defaultClientCreationRequest()).getClientId();
        Long clientId2 = clientHelper.createClient(ClientHelper.defaultClientCreationRequest()).getClientId();

        runAt("01 January 2023", () -> {
            // Create Client

            int numberOfRepayments = 3;
            int repaymentEvery = 1;

            // Create Loan Products
            PostLoanProductsRequest product = createOnePeriod30DaysLongNoInterestPeriodicAccrualProduct() //
                    .numberOfRepayments(numberOfRepayments) //
                    .repaymentEvery(repaymentEvery) //
                    .installmentAmountInMultiplesOf(null) //
                    .repaymentFrequencyType(RepaymentFrequencyType.MONTHS.longValue()) //
                    .interestType(InterestType.DECLINING_BALANCE)//
                    .interestRatePerPeriod(10.0)//
                    .interestCalculationPeriodType(InterestCalculationPeriodType.DAILY)//
                    .interestRecalculationCompoundingMethod(InterestRecalculationCompoundingMethod.NONE)//
                    .rescheduleStrategyMethod(RescheduleStrategyMethod.ADJUST_LAST_UNPAID_PERIOD)//
                    .isInterestRecalculationEnabled(true)//
                    .recalculationRestFrequencyInterval(1)//
                    .recalculationRestFrequencyType(RecalculationRestFrequencyType.DAILY)//
                    .rescheduleStrategyMethod(RescheduleStrategyMethod.REDUCE_EMI_AMOUNT)//
                    .allowPartialPeriodInterestCalcualtion(false)//
                    .disallowExpectedDisbursements(false)//
                    .allowApprovedDisbursedAmountsOverApplied(false)//
                    .overAppliedNumber(null)//
                    .overAppliedCalculationType(null)//
                    .multiDisburseLoan(null);//

            PostLoanProductsResponse loanProductResponse = loanProductHelper.createLoanProduct(product);
            Long loanProductId = loanProductResponse.getResourceId();

            PostLoanProductsRequest product2 = createOnePeriod30DaysLongNoInterestPeriodicAccrualProduct() //
                    .numberOfRepayments(numberOfRepayments) //
                    .repaymentEvery(repaymentEvery) //
                    .installmentAmountInMultiplesOf(null) //
                    .repaymentFrequencyType(RepaymentFrequencyType.MONTHS.longValue()) //
                    .interestType(InterestType.DECLINING_BALANCE)//
                    .interestRatePerPeriod(10.0)//
                    .interestCalculationPeriodType(InterestCalculationPeriodType.DAILY)//
                    .interestRecalculationCompoundingMethod(InterestRecalculationCompoundingMethod.NONE)//
                    .rescheduleStrategyMethod(RescheduleStrategyMethod.ADJUST_LAST_UNPAID_PERIOD)//
                    .isInterestRecalculationEnabled(true)//
                    .recalculationRestFrequencyInterval(1)//
                    .recalculationRestFrequencyType(RecalculationRestFrequencyType.DAILY)//
                    .rescheduleStrategyMethod(RescheduleStrategyMethod.REDUCE_EMI_AMOUNT)//
                    .allowPartialPeriodInterestCalcualtion(false)//
                    .disallowExpectedDisbursements(false)//
                    .allowApprovedDisbursedAmountsOverApplied(false)//
                    .overAppliedNumber(null)//
                    .overAppliedCalculationType(null)//
                    .multiDisburseLoan(null);//

            PostLoanProductsResponse loanProductResponse2 = loanProductHelper.createLoanProduct(product2);
            Long loanProductId2 = loanProductResponse2.getResourceId();

            // Apply and Approve Loan
            double amount = 5000.0;

            PostLoansRequest applicationRequest = applyLoanRequest(clientId, loanProductId, "01 January 2023", amount, numberOfRepayments)//
                    .repaymentEvery(repaymentEvery)//
                    .interestRatePerPeriod(BigDecimal.valueOf(10.0))//
                    .loanTermFrequency(numberOfRepayments)//
                    .repaymentFrequencyType(RepaymentFrequencyType.MONTHS)//
                    .loanTermFrequencyType(RepaymentFrequencyType.MONTHS)//
                    .interestType(InterestType.DECLINING_BALANCE)//
                    .interestCalculationPeriodType(InterestCalculationPeriodType.DAILY);//

            PostLoansRequest applicationRequest2 = applyLoanRequest(clientId2, loanProductId2, "01 January 2023", amount,
                    numberOfRepayments)//
                    .repaymentEvery(repaymentEvery)//
                    .interestRatePerPeriod(BigDecimal.valueOf(10.0))//
                    .loanTermFrequency(numberOfRepayments)//
                    .repaymentFrequencyType(RepaymentFrequencyType.MONTHS)//
                    .loanTermFrequencyType(RepaymentFrequencyType.MONTHS)//
                    .interestType(InterestType.DECLINING_BALANCE)//
                    .interestCalculationPeriodType(InterestCalculationPeriodType.DAILY);//

            PostLoansResponse postLoansResponse = loanTransactionHelper.applyLoan(applicationRequest);
            PostLoansResponse postLoansResponse2 = loanTransactionHelper.applyLoan(applicationRequest2);

            PostLoansLoanIdResponse approvedLoanResult = loanTransactionHelper.approveLoan(postLoansResponse.getResourceId(),
                    approveLoanRequest(amount, "01 January 2023"));

            PostLoansLoanIdResponse approvedLoanResult2 = loanTransactionHelper.approveLoan(postLoansResponse2.getResourceId(),
                    approveLoanRequest(amount, "01 January 2023"));

            Long loanId = approvedLoanResult.getLoanId();
            Long loanId2 = approvedLoanResult2.getLoanId();
            createdLoanId.getAndSet(loanId);
            createdLoanId2.getAndSet(loanId2);

            // disburse Loan
            disburseLoan(loanId, BigDecimal.valueOf(amount), "01 January 2023");
            disburseLoan(loanId2, BigDecimal.valueOf(amount), "01 January 2023");
        });
        runAt("01 February 2023", () -> {
            long loanId = createdLoanId.get();
            GetLoansResponse loansLoanIdResponse = loanTransactionHelper.retrieveAllLoans(null, null, clientId);
            assertThat(loansLoanIdResponse.getPageItems()).isNotNull();
            assertThat(loansLoanIdResponse.getPageItems().size()).isEqualTo(1);
            Long loanIdFromResponse = loansLoanIdResponse.getPageItems().iterator().next().getId();
            assertThat(loanIdFromResponse).isEqualTo(loanId);
        });
    }

    @Test
    public void test_retrieveLoansWithSummary_Works() {
        AtomicLong createdLoanId = new AtomicLong();

        runAt("01 January 2023", () -> {
            // Create Client
            Long clientId = clientHelper.createClient(ClientHelper.defaultClientCreationRequest()).getClientId();

            int numberOfRepayments = 3;
            int repaymentEvery = 1;

            // Create Loan Product
            PostLoanProductsRequest product = createOnePeriod30DaysLongNoInterestPeriodicAccrualProduct() //
                    .numberOfRepayments(numberOfRepayments) //
                    .repaymentEvery(repaymentEvery) //
                    .installmentAmountInMultiplesOf(null) //
                    .repaymentFrequencyType(RepaymentFrequencyType.MONTHS.longValue()) //
                    .interestType(InterestType.DECLINING_BALANCE)//
                    .interestRatePerPeriod(10.0)//
                    .interestCalculationPeriodType(InterestCalculationPeriodType.DAILY)//
                    .interestRecalculationCompoundingMethod(InterestRecalculationCompoundingMethod.NONE)//
                    .rescheduleStrategyMethod(RescheduleStrategyMethod.ADJUST_LAST_UNPAID_PERIOD)//
                    .isInterestRecalculationEnabled(true)//
                    .recalculationRestFrequencyInterval(1)//
                    .recalculationRestFrequencyType(RecalculationRestFrequencyType.DAILY)//
                    .rescheduleStrategyMethod(RescheduleStrategyMethod.REDUCE_EMI_AMOUNT)//
                    .allowPartialPeriodInterestCalcualtion(false)//
                    .disallowExpectedDisbursements(false)//
                    .allowApprovedDisbursedAmountsOverApplied(false)//
                    .overAppliedNumber(null)//
                    .overAppliedCalculationType(null)//
                    .multiDisburseLoan(null);//

            PostLoanProductsResponse loanProductResponse = loanProductHelper.createLoanProduct(product);
            Long loanProductId = loanProductResponse.getResourceId();

            // Apply and Approve Loan
            double amount = 5000.0;

            PostLoansRequest applicationRequest = applyLoanRequest(clientId, loanProductId, "01 January 2023", amount, numberOfRepayments)//
                    .repaymentEvery(repaymentEvery)//
                    .interestRatePerPeriod(BigDecimal.valueOf(10.0))//
                    .loanTermFrequency(numberOfRepayments)//
                    .repaymentFrequencyType(RepaymentFrequencyType.MONTHS)//
                    .loanTermFrequencyType(RepaymentFrequencyType.MONTHS)//
                    .interestType(InterestType.DECLINING_BALANCE)//
                    .interestCalculationPeriodType(InterestCalculationPeriodType.DAILY);//

            PostLoansResponse postLoansResponse = loanTransactionHelper.applyLoan(applicationRequest);

            PostLoansLoanIdResponse approvedLoanResult = loanTransactionHelper.approveLoan(postLoansResponse.getResourceId(),
                    approveLoanRequest(amount, "01 January 2023"));

            Long loanId = approvedLoanResult.getLoanId();
            createdLoanId.getAndSet(loanId);

            // disburse Loan
            disburseLoan(loanId, BigDecimal.valueOf(amount), "01 January 2023");
        });
        runAt("01 February 2023", () -> {
            long loanId = createdLoanId.get();
            GetLoansLoanIdResponse loanResponse = loanTransactionHelper.getLoanDetails(loanId);
            GetLoansResponse loansLoanIdResponse = loanTransactionHelper.retrieveAllLoans(loanResponse.getAccountNo(), "summary", null);
            BigDecimal totalUnpaidPayableDueInterest = loansLoanIdResponse.getPageItems().iterator().next().getSummary()
                    .getTotalUnpaidPayableDueInterest();
            assertThat(totalUnpaidPayableDueInterest).isEqualByComparingTo(BigDecimal.valueOf(509.59));
        });
    }

    @Test
    public void test_retrieveLoansWithSummaryWithoutDisbursement_Works() {
        AtomicLong createdLoanId = new AtomicLong();

        runAt("01 January 2023", () -> {
            // Create Client
            Long clientId = clientHelper.createClient(ClientHelper.defaultClientCreationRequest()).getClientId();

            int numberOfRepayments = 3;
            int repaymentEvery = 1;

            // Create Loan Product
            PostLoanProductsRequest product = createOnePeriod30DaysLongNoInterestPeriodicAccrualProduct() //
                    .numberOfRepayments(numberOfRepayments) //
                    .repaymentEvery(repaymentEvery) //
                    .installmentAmountInMultiplesOf(null) //
                    .repaymentFrequencyType(RepaymentFrequencyType.MONTHS.longValue()) //
                    .interestType(InterestType.DECLINING_BALANCE)//
                    .interestRatePerPeriod(10.0)//
                    .interestCalculationPeriodType(InterestCalculationPeriodType.DAILY)//
                    .interestRecalculationCompoundingMethod(InterestRecalculationCompoundingMethod.NONE)//
                    .rescheduleStrategyMethod(RescheduleStrategyMethod.ADJUST_LAST_UNPAID_PERIOD)//
                    .isInterestRecalculationEnabled(true)//
                    .recalculationRestFrequencyInterval(1)//
                    .recalculationRestFrequencyType(RecalculationRestFrequencyType.DAILY)//
                    .rescheduleStrategyMethod(RescheduleStrategyMethod.REDUCE_EMI_AMOUNT)//
                    .allowPartialPeriodInterestCalcualtion(false)//
                    .disallowExpectedDisbursements(false)//
                    .allowApprovedDisbursedAmountsOverApplied(false)//
                    .overAppliedNumber(null)//
                    .overAppliedCalculationType(null)//
                    .multiDisburseLoan(null);//

            PostLoanProductsResponse loanProductResponse = loanProductHelper.createLoanProduct(product);
            Long loanProductId = loanProductResponse.getResourceId();

            // Apply and Approve Loan
            double amount = 5000.0;

            PostLoansRequest applicationRequest = applyLoanRequest(clientId, loanProductId, "01 January 2023", amount, numberOfRepayments)//
                    .repaymentEvery(repaymentEvery)//
                    .interestRatePerPeriod(BigDecimal.valueOf(10.0))//
                    .loanTermFrequency(numberOfRepayments)//
                    .repaymentFrequencyType(RepaymentFrequencyType.MONTHS)//
                    .loanTermFrequencyType(RepaymentFrequencyType.MONTHS)//
                    .interestType(InterestType.DECLINING_BALANCE)//
                    .interestCalculationPeriodType(InterestCalculationPeriodType.DAILY);//

            PostLoansResponse postLoansResponse = loanTransactionHelper.applyLoan(applicationRequest);

            PostLoansLoanIdResponse approvedLoanResult = loanTransactionHelper.approveLoan(postLoansResponse.getResourceId(),
                    approveLoanRequest(amount, "01 January 2023"));

            Long loanId = approvedLoanResult.getLoanId();
            createdLoanId.getAndSet(loanId);
        });
        runAt("01 February 2023", () -> {
            long loanId = createdLoanId.get();
            GetLoansLoanIdResponse loanResponse = loanTransactionHelper.getLoanDetails(loanId);
            GetLoansResponse loansLoanIdResponse = loanTransactionHelper.retrieveAllLoans(loanResponse.getAccountNo(), "summary", null);
            assertThat(loansLoanIdResponse.getPageItems()).isNotNull();
            assertThat(loansLoanIdResponse.getPageItems().iterator().next().getSummary()).isNull();
        });
    }

    @Test
    public void retrieveAllLoans_withSecuredTrue_returnsOnlyLoansWithCollateral() {
        AtomicLong securedLoanId = new AtomicLong();
        AtomicLong unsecuredLoanId = new AtomicLong();

        runAt("01 January 2023", () -> {
            // Create Clients
            Long securedClientId = clientHelper.createClient(ClientHelper.defaultClientCreationRequest()).getClientId();
            Long unsecuredClientId = clientHelper.createClient(ClientHelper.defaultClientCreationRequest()).getClientId();

            int numberOfRepayments = 3;
            int repaymentEvery = 1;
            double amount = 5000.0;

            // Create Loan Product
            PostLoanProductsRequest product = createOnePeriod30DaysLongNoInterestPeriodicAccrualProduct()
                    .numberOfRepayments(numberOfRepayments)
                    .repaymentEvery(repaymentEvery)
                    .installmentAmountInMultiplesOf(null)
                    .repaymentFrequencyType(RepaymentFrequencyType.MONTHS.longValue())
                    .interestType(InterestType.DECLINING_BALANCE)
                    .interestRatePerPeriod(10.0)
                    .interestCalculationPeriodType(InterestCalculationPeriodType.DAILY)
                    .interestRecalculationCompoundingMethod(InterestRecalculationCompoundingMethod.NONE)
                    .rescheduleStrategyMethod(RescheduleStrategyMethod.ADJUST_LAST_UNPAID_PERIOD)
                    .isInterestRecalculationEnabled(true)
                    .recalculationRestFrequencyInterval(1)
                    .recalculationRestFrequencyType(RecalculationRestFrequencyType.DAILY)
                    .rescheduleStrategyMethod(RescheduleStrategyMethod.REDUCE_EMI_AMOUNT)
                    .allowPartialPeriodInterestCalcualtion(false)
                    .disallowExpectedDisbursements(false)
                    .allowApprovedDisbursedAmountsOverApplied(false)
                    .overAppliedNumber(null)
                    .overAppliedCalculationType(null)
                    .multiDisburseLoan(null);

            PostLoanProductsResponse loanProductResponse = loanProductHelper.createLoanProduct(product);
            Long loanProductId = loanProductResponse.getResourceId();

            // Create Collateral Product and associate with secured client
            Integer collateralProductId = CollateralManagementHelper.createCollateralProduct(requestSpec, responseSpec);
            Integer clientCollateralId = CollateralManagementHelper.createClientCollateral(requestSpec, responseSpec, 
                securedClientId.toString(), collateralProductId);

            // Apply and Approve Loans
            PostLoansRequest securedLoanApplication = applyLoanRequest(securedClientId, loanProductId, "01 January 2023", amount, numberOfRepayments)
                    .repaymentEvery(repaymentEvery)
                    .interestRatePerPeriod(BigDecimal.valueOf(10.0))
                    .loanTermFrequency(numberOfRepayments)
                    .repaymentFrequencyType(RepaymentFrequencyType.MONTHS)
                    .loanTermFrequencyType(RepaymentFrequencyType.MONTHS)
                    .interestType(InterestType.DECLINING_BALANCE)
                    .interestCalculationPeriodType(InterestCalculationPeriodType.DAILY);

            PostLoansRequest unsecuredLoanApplication = applyLoanRequest(unsecuredClientId, loanProductId, "01 January 2023", amount, numberOfRepayments)
                    .repaymentEvery(repaymentEvery)
                    .interestRatePerPeriod(BigDecimal.valueOf(10.0))
                    .loanTermFrequency(numberOfRepayments)
                    .repaymentFrequencyType(RepaymentFrequencyType.MONTHS)
                    .loanTermFrequencyType(RepaymentFrequencyType.MONTHS)
                    .interestType(InterestType.DECLINING_BALANCE)
                    .interestCalculationPeriodType(InterestCalculationPeriodType.DAILY);

            PostLoansResponse securedLoanResponse = loanTransactionHelper.applyLoan(securedLoanApplication);
            PostLoansResponse unsecuredLoanResponse = loanTransactionHelper.applyLoan(unsecuredLoanApplication);

            PostLoansLoanIdResponse securedApprovedResult = loanTransactionHelper.approveLoan(securedLoanResponse.getResourceId(),
                    approveLoanRequest(amount, "01 January 2023"));
            PostLoansLoanIdResponse unsecuredApprovedResult = loanTransactionHelper.approveLoan(unsecuredLoanResponse.getResourceId(),
                    approveLoanRequest(amount, "01 January 2023"));

            Long securedLoanIdValue = securedApprovedResult.getLoanId();
            Long unsecuredLoanIdValue = unsecuredApprovedResult.getLoanId();
            securedLoanId.getAndSet(securedLoanIdValue);
            unsecuredLoanId.getAndSet(unsecuredLoanIdValue);

            // Associate collateral with the secured loan
            // This creates a record in m_loan_collateral table
            String loanCollateralUrl = "/fineract-provider/api/v1/loans/" + securedLoanIdValue + "/collaterals";
            String loanCollateralJson = CollateralManagementHelper.clientCollateralAsJson(clientCollateralId, BigDecimal.valueOf(100));
            Utils.performServerPost(requestSpec, responseSpec, loanCollateralUrl + "?" + Utils.TENANT_IDENTIFIER, loanCollateralJson, "resourceId");

            // Disburse both loans
            disburseLoan(securedLoanIdValue, BigDecimal.valueOf(amount), "01 January 2023");
            disburseLoan(unsecuredLoanIdValue, BigDecimal.valueOf(amount), "01 January 2023");
        });

        runAt("01 February 2023", () -> {
            // Test secured=true filter - should return only loans with collateral
            String loansUrl = "/fineract-provider/api/v1/loans?secured=true&" + Utils.TENANT_IDENTIFIER;
            String jsonResponse = Utils.performServerGet(requestSpec, responseSpec, loansUrl, "");
            
            // Parse response to verify only secured loan is returned
            Gson gson = new Gson();
            GetLoansResponse securedLoansResponse = gson.fromJson(jsonResponse, GetLoansResponse.class);
            
            assertThat(securedLoansResponse.getPageItems()).isNotNull();
            assertThat(securedLoansResponse.getPageItems()).hasSize(1);
            
            Long returnedLoanId = securedLoansResponse.getPageItems().iterator().next().getId();
            assertThat(returnedLoanId).isEqualTo(securedLoanId.get());
        });
    }

    @Test
    public void retrieveAllLoans_withSecuredFalse_returnsOnlyLoansWithoutCollateral() {
        AtomicLong securedLoanId = new AtomicLong();
        AtomicLong unsecuredLoanId = new AtomicLong();

        runAt("01 January 2023", () -> {
            // Create Clients
            Long securedClientId = clientHelper.createClient(ClientHelper.defaultClientCreationRequest()).getClientId();
            Long unsecuredClientId = clientHelper.createClient(ClientHelper.defaultClientCreationRequest()).getClientId();

            int numberOfRepayments = 3;
            int repaymentEvery = 1;
            double amount = 5000.0;

            // Create Loan Product
            PostLoanProductsRequest product = createOnePeriod30DaysLongNoInterestPeriodicAccrualProduct()
                    .numberOfRepayments(numberOfRepayments)
                    .repaymentEvery(repaymentEvery)
                    .installmentAmountInMultiplesOf(null)
                    .repaymentFrequencyType(RepaymentFrequencyType.MONTHS.longValue())
                    .interestType(InterestType.DECLINING_BALANCE)
                    .interestRatePerPeriod(10.0)
                    .interestCalculationPeriodType(InterestCalculationPeriodType.DAILY)
                    .interestRecalculationCompoundingMethod(InterestRecalculationCompoundingMethod.NONE)
                    .rescheduleStrategyMethod(RescheduleStrategyMethod.ADJUST_LAST_UNPAID_PERIOD)
                    .isInterestRecalculationEnabled(true)
                    .recalculationRestFrequencyInterval(1)
                    .recalculationRestFrequencyType(RecalculationRestFrequencyType.DAILY)
                    .rescheduleStrategyMethod(RescheduleStrategyMethod.REDUCE_EMI_AMOUNT)
                    .allowPartialPeriodInterestCalcualtion(false)
                    .disallowExpectedDisbursements(false)
                    .allowApprovedDisbursedAmountsOverApplied(false)
                    .overAppliedNumber(null)
                    .overAppliedCalculationType(null)
                    .multiDisburseLoan(null);

            PostLoanProductsResponse loanProductResponse = loanProductHelper.createLoanProduct(product);
            Long loanProductId = loanProductResponse.getResourceId();

            // Create Collateral Product and associate with secured client
            Integer collateralProductId = CollateralManagementHelper.createCollateralProduct(requestSpec, responseSpec);
            Integer clientCollateralId = CollateralManagementHelper.createClientCollateral(requestSpec, responseSpec, 
                securedClientId.toString(), collateralProductId);

            // Apply and Approve Loans
            PostLoansRequest securedLoanApplication = applyLoanRequest(securedClientId, loanProductId, "01 January 2023", amount, numberOfRepayments)
                    .repaymentEvery(repaymentEvery)
                    .interestRatePerPeriod(BigDecimal.valueOf(10.0))
                    .loanTermFrequency(numberOfRepayments)
                    .repaymentFrequencyType(RepaymentFrequencyType.MONTHS)
                    .loanTermFrequencyType(RepaymentFrequencyType.MONTHS)
                    .interestType(InterestType.DECLINING_BALANCE)
                    .interestCalculationPeriodType(InterestCalculationPeriodType.DAILY);

            PostLoansRequest unsecuredLoanApplication = applyLoanRequest(unsecuredClientId, loanProductId, "01 January 2023", amount, numberOfRepayments)
                    .repaymentEvery(repaymentEvery)
                    .interestRatePerPeriod(BigDecimal.valueOf(10.0))
                    .loanTermFrequency(numberOfRepayments)
                    .repaymentFrequencyType(RepaymentFrequencyType.MONTHS)
                    .loanTermFrequencyType(RepaymentFrequencyType.MONTHS)
                    .interestType(InterestType.DECLINING_BALANCE)
                    .interestCalculationPeriodType(InterestCalculationPeriodType.DAILY);

            PostLoansResponse securedLoanResponse = loanTransactionHelper.applyLoan(securedLoanApplication);
            PostLoansResponse unsecuredLoanResponse = loanTransactionHelper.applyLoan(unsecuredLoanApplication);

            PostLoansLoanIdResponse securedApprovedResult = loanTransactionHelper.approveLoan(securedLoanResponse.getResourceId(),
                    approveLoanRequest(amount, "01 January 2023"));
            PostLoansLoanIdResponse unsecuredApprovedResult = loanTransactionHelper.approveLoan(unsecuredLoanResponse.getResourceId(),
                    approveLoanRequest(amount, "01 January 2023"));

            Long securedLoanIdValue = securedApprovedResult.getLoanId();
            Long unsecuredLoanIdValue = unsecuredApprovedResult.getLoanId();
            securedLoanId.getAndSet(securedLoanIdValue);
            unsecuredLoanId.getAndSet(unsecuredLoanIdValue);

            // Associate collateral with the secured loan only
            // This creates a record in m_loan_collateral table for the secured loan
            String loanCollateralUrl = "/fineract-provider/api/v1/loans/" + securedLoanIdValue + "/collaterals";
            String loanCollateralJson = CollateralManagementHelper.clientCollateralAsJson(clientCollateralId, BigDecimal.valueOf(100));
            Utils.performServerPost(requestSpec, responseSpec, loanCollateralUrl + "?" + Utils.TENANT_IDENTIFIER, loanCollateralJson, "resourceId");

            // Disburse both loans
            disburseLoan(securedLoanIdValue, BigDecimal.valueOf(amount), "01 January 2023");
            disburseLoan(unsecuredLoanIdValue, BigDecimal.valueOf(amount), "01 January 2023");
        });

        runAt("01 February 2023", () -> {
            // Test secured=false filter - should return only loans without collateral
            String loansUrl = "/fineract-provider/api/v1/loans?secured=false&" + Utils.TENANT_IDENTIFIER;
            String jsonResponse = Utils.performServerGet(requestSpec, responseSpec, loansUrl, "");
            
            // Parse response to verify only unsecured loan is returned
            Gson gson = new Gson();
            GetLoansResponse unsecuredLoansResponse = gson.fromJson(jsonResponse, GetLoansResponse.class);
            
            assertThat(unsecuredLoansResponse.getPageItems()).isNotNull();
            assertThat(unsecuredLoansResponse.getPageItems()).hasSize(1);
            
            Long returnedLoanId = unsecuredLoansResponse.getPageItems().iterator().next().getId();
            assertThat(returnedLoanId).isEqualTo(unsecuredLoanId.get());
        });
    }

    @Test
    public void retrieveAllLoans_withoutSecuredParameter_returnsAllLoans() {
        AtomicLong securedLoanId = new AtomicLong();
        AtomicLong unsecuredLoanId = new AtomicLong();

        runAt("01 January 2023", () -> {
            // Create Clients
            Long securedClientId = clientHelper.createClient(ClientHelper.defaultClientCreationRequest()).getClientId();
            Long unsecuredClientId = clientHelper.createClient(ClientHelper.defaultClientCreationRequest()).getClientId();

            int numberOfRepayments = 3;
            int repaymentEvery = 1;
            double amount = 5000.0;

            // Create Loan Product
            PostLoanProductsRequest product = createOnePeriod30DaysLongNoInterestPeriodicAccrualProduct()
                    .numberOfRepayments(numberOfRepayments)
                    .repaymentEvery(repaymentEvery)
                    .installmentAmountInMultiplesOf(null)
                    .repaymentFrequencyType(RepaymentFrequencyType.MONTHS.longValue())
                    .interestType(InterestType.DECLINING_BALANCE)
                    .interestRatePerPeriod(10.0)
                    .interestCalculationPeriodType(InterestCalculationPeriodType.DAILY)
                    .interestRecalculationCompoundingMethod(InterestRecalculationCompoundingMethod.NONE)
                    .rescheduleStrategyMethod(RescheduleStrategyMethod.ADJUST_LAST_UNPAID_PERIOD)
                    .isInterestRecalculationEnabled(true)
                    .recalculationRestFrequencyInterval(1)
                    .recalculationRestFrequencyType(RecalculationRestFrequencyType.DAILY)
                    .rescheduleStrategyMethod(RescheduleStrategyMethod.REDUCE_EMI_AMOUNT)
                    .allowPartialPeriodInterestCalcualtion(false)
                    .disallowExpectedDisbursements(false)
                    .allowApprovedDisbursedAmountsOverApplied(false)
                    .overAppliedNumber(null)
                    .overAppliedCalculationType(null)
                    .multiDisburseLoan(null);

            PostLoanProductsResponse loanProductResponse = loanProductHelper.createLoanProduct(product);
            Long loanProductId = loanProductResponse.getResourceId();

            // Create Collateral Product and associate with secured client
            Integer collateralProductId = CollateralManagementHelper.createCollateralProduct(requestSpec, responseSpec);
            Integer clientCollateralId = CollateralManagementHelper.createClientCollateral(requestSpec, responseSpec, 
                securedClientId.toString(), collateralProductId);

            // Apply and Approve Loans
            PostLoansRequest securedLoanApplication = applyLoanRequest(securedClientId, loanProductId, "01 January 2023", amount, numberOfRepayments)
                    .repaymentEvery(repaymentEvery)
                    .interestRatePerPeriod(BigDecimal.valueOf(10.0))
                    .loanTermFrequency(numberOfRepayments)
                    .repaymentFrequencyType(RepaymentFrequencyType.MONTHS)
                    .loanTermFrequencyType(RepaymentFrequencyType.MONTHS)
                    .interestType(InterestType.DECLINING_BALANCE)
                    .interestCalculationPeriodType(InterestCalculationPeriodType.DAILY);

            PostLoansRequest unsecuredLoanApplication = applyLoanRequest(unsecuredClientId, loanProductId, "01 January 2023", amount, numberOfRepayments)
                    .repaymentEvery(repaymentEvery)
                    .interestRatePerPeriod(BigDecimal.valueOf(10.0))
                    .loanTermFrequency(numberOfRepayments)
                    .repaymentFrequencyType(RepaymentFrequencyType.MONTHS)
                    .loanTermFrequencyType(RepaymentFrequencyType.MONTHS)
                    .interestType(InterestType.DECLINING_BALANCE)
                    .interestCalculationPeriodType(InterestCalculationPeriodType.DAILY);

            PostLoansResponse securedLoanResponse = loanTransactionHelper.applyLoan(securedLoanApplication);
            PostLoansResponse unsecuredLoanResponse = loanTransactionHelper.applyLoan(unsecuredLoanApplication);

            PostLoansLoanIdResponse securedApprovedResult = loanTransactionHelper.approveLoan(securedLoanResponse.getResourceId(),
                    approveLoanRequest(amount, "01 January 2023"));
            PostLoansLoanIdResponse unsecuredApprovedResult = loanTransactionHelper.approveLoan(unsecuredLoanResponse.getResourceId(),
                    approveLoanRequest(amount, "01 January 2023"));

            Long securedLoanIdValue = securedApprovedResult.getLoanId();
            Long unsecuredLoanIdValue = unsecuredApprovedResult.getLoanId();
            securedLoanId.getAndSet(securedLoanIdValue);
            unsecuredLoanId.getAndSet(unsecuredLoanIdValue);

            // Associate collateral with the secured loan only
            String loanCollateralUrl = "/fineract-provider/api/v1/loans/" + securedLoanIdValue + "/collaterals";
            String loanCollateralJson = CollateralManagementHelper.clientCollateralAsJson(clientCollateralId, BigDecimal.valueOf(100));
            Utils.performServerPost(requestSpec, responseSpec, loanCollateralUrl + "?" + Utils.TENANT_IDENTIFIER, loanCollateralJson, "resourceId");

            // Disburse both loans
            disburseLoan(securedLoanIdValue, BigDecimal.valueOf(amount), "01 January 2023");
            disburseLoan(unsecuredLoanIdValue, BigDecimal.valueOf(amount), "01 January 2023");
        });

        runAt("01 February 2023", () -> {
            // Test without secured parameter - should return all loans (backward compatibility)
            String loansUrl = "/fineract-provider/api/v1/loans?" + Utils.TENANT_IDENTIFIER;
            String jsonResponse = Utils.performServerGet(requestSpec, responseSpec, loansUrl, "");
            
            // Parse response to verify all loans are returned
            Gson gson = new Gson();
            GetLoansResponse allLoansResponse = gson.fromJson(jsonResponse, GetLoansResponse.class);
            
            assertThat(allLoansResponse.getPageItems()).isNotNull();
            assertThat(allLoansResponse.getPageItems()).hasSizeGreaterThanOrEqualTo(2);
            
            // Verify both loan IDs are present in the response
            boolean securedLoanFound = allLoansResponse.getPageItems().stream()
                    .anyMatch(loan -> loan.getId().equals(securedLoanId.get()));
            boolean unsecuredLoanFound = allLoansResponse.getPageItems().stream()
                    .anyMatch(loan -> loan.getId().equals(unsecuredLoanId.get()));
            
            assertThat(securedLoanFound).isTrue();
            assertThat(unsecuredLoanFound).isTrue();
        });
    }
}
