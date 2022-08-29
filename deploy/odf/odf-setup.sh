# ODF
oc create namespace openshift-storage
oc apply -f ./odf-operatorgroup.yaml

#oc get OperatorGroup -n openshift-storage -0 json

##oc wait --for=jsonpath='{.status.conditions[0].type}'=1 dc -l app=postgresql

oc apply -f ./odf-operator-sub.yaml
oc wait --for=jsonpath='{.status.catalogHealth[0].healthy}'=true subscription/mcg-operator-stable-4.10-redhat-operators-openshift-marketplace -n openshift-storage 

## after deploying the odf operator subscription, you need to ensure the other operators are deployed
## oc get subscription -n openshift-storage
## NAME                                                                         PACKAGE                   SOURCE             CHANNEL
## mcg-operator-stable-4.10-redhat-operators-openshift-marketplace              mcg-operator              redhat-operators   stable-4.10
## ocs-operator-stable-4.10-redhat-operators-openshift-marketplace              ocs-operator              redhat-operators   stable-4.10
## odf-csi-addons-operator-stable-4.10-redhat-operators-openshift-marketplace   odf-csi-addons-operator   redhat-operators   stable-4.10
## odf-operator                                                                 odf-operator              redhat-operators   stable-4.10

oc apply -f ./mcg-storagesystem.yaml
## once storage system is available, obtain the uid
# Need uid to include in storagecluster resource
oc wait --for=jsonpath='{.status.conditions[0].status}'=True storagesystem/ocs-storagecluster-storagesystem -n openshift-storage 

oc get storagesystem/ocs-storagecluster-storagesystem -n openshift-storage -o jsonpath='{.status.conditions[0].status}'

storagesystemuid=$(oc get storagesystem/ocs-storagecluster-storagesystem -n openshift-storage -o jsonpath='{.metadata.uid}')

perl -pe "s|(STORAGE_SYSTEM_UID).*$|${storagesystemuid} |" -i ./mcg-storagecluster.yaml

oc apply -f mcg-storagecluster.yaml
