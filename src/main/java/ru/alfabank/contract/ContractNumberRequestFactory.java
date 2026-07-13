package ru.alfabank.contract;

import ru.alfabank.contract.dto.ContractNumberRequest;

public final class ContractNumberRequestFactory {

    private ContractNumberRequestFactory() {
    }

    public static ContractNumberRequest byProgramId(long programId) {
        ContractNumberRequest request = new ContractNumberRequest();
        request.setProgramId(programId);
        return request;
    }
}
