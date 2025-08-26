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

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.Response;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
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
    public void retrieveAllLoans_withSecuredTrue_returnsOnlyLoansWithCollateral() {
        AtomicLong securedLoanId = new AtomicLong();
        AtomicLong unsecuredLoanId = new AtomicLong();

        runAt("01 January 2023", () -> {
            // Create Clients
            Long clientId1 = clientHelper.createClient(ClientHelper.defaultClientCreationRequest()).getClientId();
            Long clientId2 = clientHelper.createClient(ClientHelper.defaultClientCreationRequest()).getClientId();

            int numberOfRepayments = 3;
            int repaymentEvery = 1;

            // Create Loan Product
            PostLoanProductsRequest product = createOnePeriod30DaysLongNoInterestPeriodicAccrualProduct()
                    .numberOfRepayments(numberOfRepayments).repaymentEvery(repaymentEvery).installmentAmountInMultiplesOf(null)
                    .repaymentFrequencyType(RepaymentFrequencyType.MONTHS.longValue()).interestType(InterestType.DECLINING_BALANCE)
                    .interestRatePerPeriod(10.0).interestCalculationPeriodType(InterestCalculationPeriodType.DAILY)
                    .interestRecalculationCompoundingMethod(InterestRecalculationCompoundingMethod.NONE)
                    .rescheduleStrategyMethod(RescheduleStrategyMethod.ADJUST_LAST_UNPAID_PERIOD).isInterestRecalculationEnabled(true)
                    .recalculationRestFrequencyInterval(1).recalculationRestFrequencyType(RecalculationRestFrequencyType.DAILY)
                    .rescheduleStrategyMethod(RescheduleStrategyMethod.REDUCE_EMI_AMOUNT).allowPartialPeriodInterestCalcualtion(false)
                    .disallowExpectedDisbursements(false).allowApprovedDisbursedAmountsOverApplied(false).overAppliedNumber(null)
                    .overAppliedCalculationType(null).multiDisburseLoan(null);

            PostLoanProductsResponse loanProductResponse = loanProductHelper.createLoanProduct(product);
            Long loanProductId = loanProductResponse.getResourceId();

            // Apply and Approve Loans
            double amount = 5000.0;

            // Secured Loan (will have collateral)
            PostLoansRequest securedLoanRequest = applyLoanRequest(clientId1, loanProductId, "01 January 2023", amount, numberOfRepayments)
                    .repaymentEvery(repaymentEvery).interestRatePerPeriod(BigDecimal.valueOf(10.0)).loanTermFrequency(numberOfRepayments)
                    .repaymentFrequencyType(RepaymentFrequencyType.MONTHS).loanTermFrequencyType(RepaymentFrequencyType.MONTHS)
                    .interestType(InterestType.DECLINING_BALANCE).interestCalculationPeriodType(InterestCalculationPeriodType.DAILY);

            // Unsecured Loan (will not have collateral)
            PostLoansRequest unsecuredLoanRequest = applyLoanRequest(clientId2, loanProductId, "01 January 2023", amount,
                    numberOfRepayments).repaymentEvery(repaymentEvery).interestRatePerPeriod(BigDecimal.valueOf(10.0))
                    .loanTermFrequency(numberOfRepayments).repaymentFrequencyType(RepaymentFrequencyType.MONTHS)
                    .loanTermFrequencyType(RepaymentFrequencyType.MONTHS).interestType(InterestType.DECLINING_BALANCE)
                    .interestCalculationPeriodType(InterestCalculationPeriodType.DAILY);

            PostLoansResponse securedLoanResponse = loanTransactionHelper.applyLoan(securedLoanRequest);
            PostLoansResponse unsecuredLoanResponse = loanTransactionHelper.applyLoan(unsecuredLoanRequest);

            PostLoansLoanIdResponse approvedSecuredLoan = loanTransactionHelper.approveLoan(securedLoanResponse.getResourceId(),
                    approveLoanRequest(amount, "01 January 2023"));
            PostLoansLoanIdResponse approvedUnsecuredLoan = loanTransactionHelper.approveLoan(unsecuredLoanResponse.getResourceId(),
                    approveLoanRequest(amount, "01 January 2023"));

            Long securedLoanIdValue = approvedSecuredLoan.getLoanId();
            Long unsecuredLoanIdValue = approvedUnsecuredLoan.getLoanId();

            securedLoanId.set(securedLoanIdValue);
            unsecuredLoanId.set(unsecuredLoanIdValue);

            // Disburse Loans
            disburseLoan(securedLoanIdValue, BigDecimal.valueOf(amount), "01 January 2023");
            disburseLoan(unsecuredLoanIdValue, BigDecimal.valueOf(amount), "01 January 2023");

            // Add collateral to the secured loan using REST API
            // Create collateral data through the collateral API endpoint
            String collateralJson = "{\"type\": 1, \"value\": 10000, \"description\": \"Test Collateral\"}";
            given().spec(this.requestSpec).contentType("application/json").body(collateralJson).when()
                    .post("/v1/loans/" + securedLoanIdValue + "/collaterals").then().statusCode(200);
        });

        runAt("01 February 2023", () -> {
            // Test secured=true filter using direct HTTP call
            Response response = given().spec(this.requestSpec).when().get("/v1/loans?secured=true").then().statusCode(200).extract()
                    .response();

            // Parse response to get the loan IDs
            List<Map<String, Object>> pageItems = response.jsonPath().getList("pageItems");
            assertThat(pageItems).isNotNull();
            assertThat(pageItems.size()).isEqualTo(1);

            // Verify only the secured loan is returned
            Integer returnedLoanId = (Integer) pageItems.get(0).get("id");
            assertThat(returnedLoanId.longValue()).isEqualTo(securedLoanId.get());
        });
    }

    @Test
    public void retrieveAllLoans_withSecuredFalse_returnsOnlyLoansWithoutCollateral() {
        AtomicLong securedLoanId = new AtomicLong();
        AtomicLong unsecuredLoanId = new AtomicLong();

        runAt("01 January 2023", () -> {
            // Create Clients
            Long clientId1 = clientHelper.createClient(ClientHelper.defaultClientCreationRequest()).getClientId();
            Long clientId2 = clientHelper.createClient(ClientHelper.defaultClientCreationRequest()).getClientId();

            int numberOfRepayments = 3;
            int repaymentEvery = 1;

            // Create Loan Product
            PostLoanProductsRequest product = createOnePeriod30DaysLongNoInterestPeriodicAccrualProduct()
                    .numberOfRepayments(numberOfRepayments).repaymentEvery(repaymentEvery).installmentAmountInMultiplesOf(null)
                    .repaymentFrequencyType(RepaymentFrequencyType.MONTHS.longValue()).interestType(InterestType.DECLINING_BALANCE)
                    .interestRatePerPeriod(10.0).interestCalculationPeriodType(InterestCalculationPeriodType.DAILY)
                    .interestRecalculationCompoundingMethod(InterestRecalculationCompoundingMethod.NONE)
                    .rescheduleStrategyMethod(RescheduleStrategyMethod.ADJUST_LAST_UNPAID_PERIOD).isInterestRecalculationEnabled(true)
                    .recalculationRestFrequencyInterval(1).recalculationRestFrequencyType(RecalculationRestFrequencyType.DAILY)
                    .rescheduleStrategyMethod(RescheduleStrategyMethod.REDUCE_EMI_AMOUNT).allowPartialPeriodInterestCalcualtion(false)
                    .disallowExpectedDisbursements(false).allowApprovedDisbursedAmountsOverApplied(false).overAppliedNumber(null)
                    .overAppliedCalculationType(null).multiDisburseLoan(null);

            PostLoanProductsResponse loanProductResponse = loanProductHelper.createLoanProduct(product);
            Long loanProductId = loanProductResponse.getResourceId();

            // Apply and Approve Loans
            double amount = 5000.0;

            // Secured Loan (will have collateral)
            PostLoansRequest securedLoanRequest = applyLoanRequest(clientId1, loanProductId, "01 January 2023", amount, numberOfRepayments)
                    .repaymentEvery(repaymentEvery).interestRatePerPeriod(BigDecimal.valueOf(10.0)).loanTermFrequency(numberOfRepayments)
                    .repaymentFrequencyType(RepaymentFrequencyType.MONTHS).loanTermFrequencyType(RepaymentFrequencyType.MONTHS)
                    .interestType(InterestType.DECLINING_BALANCE).interestCalculationPeriodType(InterestCalculationPeriodType.DAILY);

            // Unsecured Loan (will not have collateral)
            PostLoansRequest unsecuredLoanRequest = applyLoanRequest(clientId2, loanProductId, "01 January 2023", amount,
                    numberOfRepayments).repaymentEvery(repaymentEvery).interestRatePerPeriod(BigDecimal.valueOf(10.0))
                    .loanTermFrequency(numberOfRepayments).repaymentFrequencyType(RepaymentFrequencyType.MONTHS)
                    .loanTermFrequencyType(RepaymentFrequencyType.MONTHS).interestType(InterestType.DECLINING_BALANCE)
                    .interestCalculationPeriodType(InterestCalculationPeriodType.DAILY);

            PostLoansResponse securedLoanResponse = loanTransactionHelper.applyLoan(securedLoanRequest);
            PostLoansResponse unsecuredLoanResponse = loanTransactionHelper.applyLoan(unsecuredLoanRequest);

            PostLoansLoanIdResponse approvedSecuredLoan = loanTransactionHelper.approveLoan(securedLoanResponse.getResourceId(),
                    approveLoanRequest(amount, "01 January 2023"));
            PostLoansLoanIdResponse approvedUnsecuredLoan = loanTransactionHelper.approveLoan(unsecuredLoanResponse.getResourceId(),
                    approveLoanRequest(amount, "01 January 2023"));

            Long securedLoanIdValue = approvedSecuredLoan.getLoanId();
            Long unsecuredLoanIdValue = approvedUnsecuredLoan.getLoanId();

            securedLoanId.set(securedLoanIdValue);
            unsecuredLoanId.set(unsecuredLoanIdValue);

            // Disburse Loans
            disburseLoan(securedLoanIdValue, BigDecimal.valueOf(amount), "01 January 2023");
            disburseLoan(unsecuredLoanIdValue, BigDecimal.valueOf(amount), "01 January 2023");

            // Add collateral to the secured loan using REST API
            // Create collateral data through the collateral API endpoint
            String collateralJson = "{\"type\": 1, \"value\": 10000, \"description\": \"Test Collateral\"}";
            given().spec(this.requestSpec).contentType("application/json").body(collateralJson).when()
                    .post("/v1/loans/" + securedLoanIdValue + "/collaterals").then().statusCode(200);
        });

        runAt("01 February 2023", () -> {
            // Test secured=false filter - should return only the loan without collateral
            Response response = given().spec(this.requestSpec).when().get("/v1/loans?secured=false").then().statusCode(200).extract()
                    .response();

            // Parse response to get the loan IDs
            List<Map<String, Object>> pageItems = response.jsonPath().getList("pageItems");
            assertThat(pageItems).isNotNull();
            assertThat(pageItems.size()).isEqualTo(1);

            // Verify only the unsecured loan is returned
            Integer returnedLoanId = (Integer) pageItems.get(0).get("id");
            assertThat(returnedLoanId.longValue()).isEqualTo(unsecuredLoanId.get());
        });
    }

    @Test
    public void retrieveAllLoans_withoutSecuredParameter_returnsAllLoans() {
        AtomicLong securedLoanId = new AtomicLong();
        AtomicLong unsecuredLoanId = new AtomicLong();

        runAt("01 January 2023", () -> {
            // Create Clients
            Long clientId1 = clientHelper.createClient(ClientHelper.defaultClientCreationRequest()).getClientId();
            Long clientId2 = clientHelper.createClient(ClientHelper.defaultClientCreationRequest()).getClientId();

            int numberOfRepayments = 3;
            int repaymentEvery = 1;

            // Create Loan Product
            PostLoanProductsRequest product = createOnePeriod30DaysLongNoInterestPeriodicAccrualProduct()
                    .numberOfRepayments(numberOfRepayments).repaymentEvery(repaymentEvery).installmentAmountInMultiplesOf(null)
                    .repaymentFrequencyType(RepaymentFrequencyType.MONTHS.longValue()).interestType(InterestType.DECLINING_BALANCE)
                    .interestRatePerPeriod(10.0).interestCalculationPeriodType(InterestCalculationPeriodType.DAILY)
                    .interestRecalculationCompoundingMethod(InterestRecalculationCompoundingMethod.NONE)
                    .rescheduleStrategyMethod(RescheduleStrategyMethod.ADJUST_LAST_UNPAID_PERIOD).isInterestRecalculationEnabled(true)
                    .recalculationRestFrequencyInterval(1).recalculationRestFrequencyType(RecalculationRestFrequencyType.DAILY)
                    .rescheduleStrategyMethod(RescheduleStrategyMethod.REDUCE_EMI_AMOUNT).allowPartialPeriodInterestCalcualtion(false)
                    .disallowExpectedDisbursements(false).allowApprovedDisbursedAmountsOverApplied(false).overAppliedNumber(null)
                    .overAppliedCalculationType(null).multiDisburseLoan(null);

            PostLoanProductsResponse loanProductResponse = loanProductHelper.createLoanProduct(product);
            Long loanProductId = loanProductResponse.getResourceId();

            // Apply and Approve Loans
            double amount = 5000.0;

            // Secured Loan (will have collateral)
            PostLoansRequest securedLoanRequest = applyLoanRequest(clientId1, loanProductId, "01 January 2023", amount, numberOfRepayments)
                    .repaymentEvery(repaymentEvery).interestRatePerPeriod(BigDecimal.valueOf(10.0)).loanTermFrequency(numberOfRepayments)
                    .repaymentFrequencyType(RepaymentFrequencyType.MONTHS).loanTermFrequencyType(RepaymentFrequencyType.MONTHS)
                    .interestType(InterestType.DECLINING_BALANCE).interestCalculationPeriodType(InterestCalculationPeriodType.DAILY);

            // Unsecured Loan (will not have collateral)
            PostLoansRequest unsecuredLoanRequest = applyLoanRequest(clientId2, loanProductId, "01 January 2023", amount,
                    numberOfRepayments).repaymentEvery(repaymentEvery).interestRatePerPeriod(BigDecimal.valueOf(10.0))
                    .loanTermFrequency(numberOfRepayments).repaymentFrequencyType(RepaymentFrequencyType.MONTHS)
                    .loanTermFrequencyType(RepaymentFrequencyType.MONTHS).interestType(InterestType.DECLINING_BALANCE)
                    .interestCalculationPeriodType(InterestCalculationPeriodType.DAILY);

            PostLoansResponse securedLoanResponse = loanTransactionHelper.applyLoan(securedLoanRequest);
            PostLoansResponse unsecuredLoanResponse = loanTransactionHelper.applyLoan(unsecuredLoanRequest);

            PostLoansLoanIdResponse approvedSecuredLoan = loanTransactionHelper.approveLoan(securedLoanResponse.getResourceId(),
                    approveLoanRequest(amount, "01 January 2023"));
            PostLoansLoanIdResponse approvedUnsecuredLoan = loanTransactionHelper.approveLoan(unsecuredLoanResponse.getResourceId(),
                    approveLoanRequest(amount, "01 January 2023"));

            Long securedLoanIdValue = approvedSecuredLoan.getLoanId();
            Long unsecuredLoanIdValue = approvedUnsecuredLoan.getLoanId();

            securedLoanId.set(securedLoanIdValue);
            unsecuredLoanId.set(unsecuredLoanIdValue);

            // Disburse Loans
            disburseLoan(securedLoanIdValue, BigDecimal.valueOf(amount), "01 January 2023");
            disburseLoan(unsecuredLoanIdValue, BigDecimal.valueOf(amount), "01 January 2023");

            // Add collateral to the secured loan using REST API
            // Create collateral data through the collateral API endpoint
            String collateralJson = "{\"type\": 1, \"value\": 10000, \"description\": \"Test Collateral\"}";
            given().spec(this.requestSpec).contentType("application/json").body(collateralJson).when()
                    .post("/v1/loans/" + securedLoanIdValue + "/collaterals").then().statusCode(200);
        });

        runAt("01 February 2023", () -> {
            // Test no secured parameter - should return both loans (backward compatibility)
            Response response = given().spec(this.requestSpec).when().get("/v1/loans").then().statusCode(200).extract().response();

            // Parse response to get the loan IDs
            List<Map<String, Object>> pageItems = response.jsonPath().getList("pageItems");
            assertThat(pageItems).isNotNull();
            assertThat(pageItems.size()).isGreaterThanOrEqualTo(2);

            // Verify both loans are present
            List<Integer> loanIds = pageItems.stream().map(item -> (Integer) item.get("id")).toList();

            long expectedSecuredId = securedLoanId.get();
            long expectedUnsecuredId = unsecuredLoanId.get();
            assertThat(loanIds.stream().anyMatch(id -> id.longValue() == expectedSecuredId)).isTrue();
            assertThat(loanIds.stream().anyMatch(id -> id.longValue() == expectedUnsecuredId)).isTrue();
        });
    }

    @Test
    public void retrieveAllLoans_withInvalidSecuredValue_returns400BadRequest() {
        runAt("01 January 2023", () -> {
            // Test invalid secured parameter value - should return 400 Bad Request
            given().spec(this.requestSpec).when().get("/v1/loans?secured=maybe").then().statusCode(400);
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
}
